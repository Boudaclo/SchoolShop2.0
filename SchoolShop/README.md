# SchoolShop (Kotlin + Jetpack Compose)

1. Ouvrir le dossier `SchoolShop` dans Android Studio (Koala ou plus récent, JDK 17).
2. Laisser la synchronisation Gradle se terminer (Android Studio crée le wrapper).
3. Lancer sur un émulateur ou un téléphone (minSdk 24).

Écrans : démarrage, onboarding, accueil (recherche), catégories, liste de produits,
détail produit, panier, livraison/paiement, confirmation, compte.
Les données sont locales (`data/Models.kt`) ; le panier est géré par `ShopViewModel`.

## Base de données locale (Room)
Room stocke sur le téléphone : panier, favoris, adresse de livraison et historique des commandes
(`data/local/` : entités, DAO, `ShopDatabase`). Le catalogue (catégories et produits) est aussi dans Room : `SampleData` sert de données de départ, réécrites dans la base à chaque lancement. La base est en version 3 (migrations incluses). Au lancement, le catalogue est synchronisé : ajouts, modifications et suppressions (et le panier/favoris orphelins).

## Images (Coil)
Dépose `1.jpg` … `19.jpg` dans `app/src/main/assets/products/` (voir `LISEZMOI.txt`), ou mets une URL dans `imageUrl`. Sans image, l'emoji reste affiché.
Tout survit à la fermeture de l'app. Le fichier `schoolshop.sql` sert de modèle si tu ajoutes un backend plus tard.
