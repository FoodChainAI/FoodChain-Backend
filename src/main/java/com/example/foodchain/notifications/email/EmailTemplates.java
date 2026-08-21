package com.example.foodchain.notifications.email;

/**
 * Inline-styled HTML email templates (brand: deep green + amber).
 * Kept dependency-free for portability; swap for a templating engine if needed.
 */
final class EmailTemplates {

    private EmailTemplates() {
    }

    private static final String FOREST = "#027a48";
    private static final String FOREST_DARK = "#052e1a";
    private static final String GOLD = "#f79009";

    private static String layout(String title, String bodyHtml) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <body style="margin:0;padding:0;background:#fbfaf7;font-family:Arial,Helvetica,sans-serif;color:#14201a;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#fbfaf7;padding:32px 16px;">
                    <tr><td align="center">
                      <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:520px;background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 24px -12px rgba(5,46,26,0.16);">
                        <tr><td style="background:%s;padding:28px 32px;">
                          <span style="color:#ffffff;font-size:20px;font-weight:bold;">FoodChain<span style="color:%s;"> AI</span></span>
                        </td></tr>
                        <tr><td style="padding:32px;">
                          <h1 style="margin:0 0 16px;font-size:22px;color:%s;">%s</h1>
                          %s
                        </td></tr>
                        <tr><td style="padding:20px 32px;border-top:1px solid #eee;color:#8a8a8a;font-size:12px;">
                          FoodChain AI — Place de marché agroalimentaire.<br/>
                          Vous recevez cet email car vous avez interagi avec FoodChain AI.
                        </td></tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(FOREST_DARK, GOLD, FOREST, title, bodyHtml);
    }

    private static String button(String href, String label) {
        return """
                <a href="%s" style="display:inline-block;background:%s;color:#052e1a;font-weight:bold;
                   text-decoration:none;padding:14px 28px;border-radius:14px;margin-top:8px;">%s</a>
                """.formatted(href, GOLD, label);
    }

    static String welcome(String email, String role) {
        String body = """
                <p style="font-size:15px;line-height:1.6;color:#3f4a44;">
                  Bonjour,<br/><br/>
                  Votre compte <strong>%s</strong> est prêt. Bienvenue dans la place de marché
                  qui connecte producteurs et acheteurs à travers l'Afrique — même en connexion limitée.
                </p>
                <p style="font-size:15px;line-height:1.6;color:#3f4a44;">
                  Vous pouvez dès maintenant explorer le catalogue, publier vos récoltes ou passer commande.
                </p>
                %s
                """.formatted(role, button("http://localhost:3000/catalogue", "Ouvrir FoodChain AI"));
        return layout("Bienvenue " + email + " 👋", body);
    }

    static String newsletter(String email) {
        String body = """
                <p style="font-size:15px;line-height:1.6;color:#3f4a44;">
                  Merci&nbsp;! L'adresse <strong>%s</strong> est bien inscrite à la newsletter FoodChain AI.
                </p>
                <p style="font-size:15px;line-height:1.6;color:#3f4a44;">
                  Vous recevrez nos actualités : nouvelles offres, conseils pour réduire les pertes
                  post-récolte et évolutions de la plateforme.
                </p>
                %s
                """.formatted(email, button("http://localhost:3000", "Découvrir la plateforme"));
        return layout("Inscription confirmée", body);
    }
}
