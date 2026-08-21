-- Seed data for development and testing
-- All passwords = "password123" (BCrypt strength 10)

INSERT INTO users (id, email, password_hash, role, verified, created_at) VALUES
  ('00000000-0000-0000-0000-000000000001', 'admin@foodchain.ai',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true, now()),
  ('00000000-0000-0000-0000-000000000002', 'farmer@foodchain.ai',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'AGRICULTEUR', true, now()),
  ('00000000-0000-0000-0000-000000000003', 'resto@foodchain.ai',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'RESTAURANT', false, now()),
  ('00000000-0000-0000-0000-000000000004', 'grossiste@foodchain.ai',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GROSSISTE', false, now()),
  ('00000000-0000-0000-0000-000000000005', 'supermarche@foodchain.ai',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SUPERMARCHE', false, now())
ON CONFLICT DO NOTHING;

-- Produits de référence
INSERT INTO products (id, name, category, unit, base_price) VALUES
  ('10000000-0000-0000-0000-000000000001', 'Maïs',              'Céréales',   'kg',     350.00),
  ('10000000-0000-0000-0000-000000000002', 'Riz local',         'Céréales',   'kg',     500.00),
  ('10000000-0000-0000-0000-000000000003', 'Tomates fraîches',  'Légumes',    'kg',     600.00),
  ('10000000-0000-0000-0000-000000000004', 'Oignons',           'Légumes',    'kg',     400.00),
  ('10000000-0000-0000-0000-000000000005', 'Bananes plantain',  'Fruits',     'régime', 1500.00),
  ('10000000-0000-0000-0000-000000000006', 'Manioc',            'Tubercules', 'kg',     250.00),
  ('10000000-0000-0000-0000-000000000007', 'Igname',            'Tubercules', 'kg',     450.00),
  ('10000000-0000-0000-0000-000000000008', 'Poulet fermier',    'Volaille',   'kg',     3500.00),
  ('10000000-0000-0000-0000-000000000009', 'Piment',            'Légumes',    'kg',     800.00),
  ('10000000-0000-0000-0000-000000000010', 'Arachides',         'Légumineuses','kg',    700.00)
ON CONFLICT DO NOTHING;

-- Offres de l'agriculteur vérifié
INSERT INTO offers (id, seller_id, product_id, quantity, price, available, location, version, created_at) VALUES
  ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000001', 500.00, 300.00, true, 'Yamoussoukro, Côte d''Ivoire', 0, now()),
  ('20000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000003', 200.00, 550.00, true, 'Abidjan, Côte d''Ivoire', 0, now()),
  ('20000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000005', 100.00, 1200.00, true, 'San Pedro, Côte d''Ivoire', 0, now()),
  ('20000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000006', 300.00, 200.00, true, 'Bouaké, Côte d''Ivoire', 0, now()),
  ('20000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000007', 150.00, 420.00, true, 'Man, Côte d''Ivoire', 0, now())
ON CONFLICT DO NOTHING;

-- Historique de prix initial
INSERT INTO price_history (id, product_id, price, ts) VALUES
  (gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 300.00, now()),
  (gen_random_uuid(), '10000000-0000-0000-0000-000000000003', 550.00, now()),
  (gen_random_uuid(), '10000000-0000-0000-0000-000000000005', 1200.00, now()),
  (gen_random_uuid(), '10000000-0000-0000-0000-000000000006', 200.00, now()),
  (gen_random_uuid(), '10000000-0000-0000-0000-000000000007', 420.00, now())
ON CONFLICT DO NOTHING;
