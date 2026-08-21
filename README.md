# FoodChain AI — Backend

Monolithe modulaire Spring Boot pour la place de marché agroalimentaire africaine.

## Démarrage rapide

### Avec Docker Compose

```bash
cd foodchain-back
docker-compose up --build
```

L'application démarre sur **http://localhost:8080**
Swagger UI : **http://localhost:8080/swagger-ui.html**
OpenAPI JSON : **http://localhost:8080/v3/api-docs**

### En local (développement)

Prérequis : Java 21+, Maven 3.9+, PostgreSQL 16.

```bash
# 1. Démarrer uniquement PostgreSQL
docker-compose up -d postgres

# 2. Lancer l'application
./mvnw spring-boot:run
```

Flyway applique automatiquement `V1__init_schema.sql` et `V2__seed_data.sql` au démarrage.

### Tests

```bash
./mvnw test
```

Les tests d'intégration utilisent **Testcontainers** — Docker doit être disponible.

```
Tests couverts :
  OrderStateTransitionTest   — machine à états, transitions invalides rejetées
  PaymentAtomicityTest       — atomicité paiement/stock (à la livraison + Mobile Money)
  ConcurrentOversellTest     — prévention survente (SELECT FOR UPDATE, 5 acheteurs concurrents)
```

## Variables d'environnement

| Variable | Défaut (dev) | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/foodchain` | URL JDBC PostgreSQL |
| `DB_USER` | `foodchain` | Utilisateur DB |
| `DB_PASSWORD` | `foodchain` | Mot de passe DB |
| `JWT_SECRET` | *(clé dev Base64 256-bit)* | **À remplacer en production** |
| `JWT_EXPIRATION_MS` | `86400000` | Durée de vie du JWT (ms) — 24h |
| `SERVER_PORT` | `8080` | Port HTTP |
| `foodchain.payments.provider` | `mock` | Fournisseur paiement (`mock` \| `mtn`) |

## Comptes de test (seed V2)

Mot de passe commun : **`password123`**

| Email | Rôle | Vérifié |
|---|---|---|
| admin@foodchain.ai | ADMIN | ✓ |
| farmer@foodchain.ai | AGRICULTEUR | ✓ |
| resto@foodchain.ai | RESTAURANT | — |
| grossiste@foodchain.ai | GROSSISTE | — |
| supermarche@foodchain.ai | SUPERMARCHE | — |

## Architecture modulaire

```
com.example.foodchain/
├── common/         # Sécurité JWT, error handling, OpenAPI config
├── users/          # Inscription, connexion, rôles
├── catalog/        # Produits, offres, stocks, historique prix
├── orders/         # Commandes, machine à états, cohérence stocks
├── payments/       # Paiement Mobile Money, provider abstrait
├── trust/          # Avis, notations
└── notifications/  # Emails Resend (bienvenue, newsletter) — voir ../EMAIL.md
```

**Règle inter-modules :** les modules communiquent exclusivement via leurs services publics. Aucun accès direct aux repositories d'un autre module.

## Règles métier critiques

| Règle | Implémentation |
|---|---|
| Stock non décrémenté à la création | `OrderService.create()` — vérification seule |
| Décrément + passage PAYEE atomiques | `PaymentService.settle()` dans une transaction |
| Anti-survente concurrent | `SELECT FOR UPDATE` via `@Lock(PESSIMISTIC_WRITE)` |
| Transitions d'état valides seulement | `OrderStatus.canTransitionTo()` + `INVALID_TRANSITION` |
| Review uniquement sur commande LIVREE | `OrderService.requireDeliveredOrder()` |

## API REST (extrait)

| Méthode | Route | Rôle | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Créer un compte | Public |
| POST | `/api/v1/auth/login` | Connexion → JWT | Public |
| GET | `/api/v1/products` | Lister les produits | Public |
| GET | `/api/v1/offers` | Rechercher des offres | Public |
| POST | `/api/v1/offers` | Publier une offre | AGRICULTEUR |
| PATCH | `/api/v1/offers/{id}/stock` | Mettre à jour le stock | AGRICULTEUR |
| POST | `/api/v1/orders` | Passer une commande | Acheteur |
| POST | `/api/v1/orders/{id}/confirm` | Confirmer | AGRICULTEUR |
| POST | `/api/v1/payments` | Initier un paiement | Acheteur |
| POST | `/api/v1/payments/callback` | Callback passerelle | Public |
| POST | `/api/v1/reviews` | Laisser un avis | Acheteur |

Documentation complète : Swagger UI au démarrage.
