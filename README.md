# SchoolShop (Kotlin + Jetpack Compose)

1. Ouvrir le dossier `SchoolShop` dans Android Studio (Koala ou plus récent, JDK 17).
2. Laisser la synchronisation Gradle se terminer (Android Studio crée le wrapper).
3. Lancer sur un émulateur ou un téléphone (minSdk 24).

Écrans : démarrage, onboarding, accueil (recherche), catégories, liste de produits,
détail produit, panier, livraison/paiement, confirmation, compte.
Les données sont locales (`data/Models.kt`) ; le panier est géré par `ShopViewModel`.

## Base de données locale (Room)
Room stocke sur le téléphone : panier, favoris, adresse de livraison et historique des commandes
(`data/local/` : entités, DAO, `ShopDatabase`). Le catalogue (catégories et produits) est aussi dans Room : `SampleData` sert de données de départ, réécrites dans la base à chaque lancement. La base est en version 4 (migrations incluses) : adresses, moyens de paiement (jamais de numéro de carte complet), favoris, commandes. Au lancement, le catalogue est synchronisé : ajouts, modifications et suppressions (et le panier/favoris orphelins).

## Images (Coil)
Dépose `1.jpg` … `19.jpg` dans `app/src/main/assets/products/` (voir `LISEZMOI.txt`), ou mets une URL dans `imageUrl`. Sans image, l'emoji reste affiché.
Tout survit à la fermeture de l'app. Le fichier `schoolshop.sql` sert de modèle si tu ajoutes un backend plus tard.

## Compte
Mes commandes, Mes adresses (adresse par défaut), Mes moyens de paiement, Mes favoris, Aide & support,
À propos, profil modifiable (⚙). Il n'y a pas de compte en ligne : « Se déconnecter » efface les données locales.
Pense à mettre les vraies coordonnées du support dans `SupportConfig` (`ui/screens/SupportScreen.kt`).
