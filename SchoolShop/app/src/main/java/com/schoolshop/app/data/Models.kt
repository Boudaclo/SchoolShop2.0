package com.schoolshop.app.data

data class Category(val id: String, val name: String, val subtitle: String, val emoji: String)

data class Product(
    val id: Int,
    val categoryId: String,
    val name: String,
    val format: String,
    val price: Int,
    val emoji: String,
    val rating: Float = 4f,
    val reviews: Int = 24,
    val description: String = "$name, $format. Qualité scolaire, idéal pour les cours et les travaux.",
    val features: List<String> = listOf(format, "Qualité scolaire", "Solide et durable"),
    val isPopular: Boolean = false,
    // Par défaut : assets/products/<id>.jpg. Peut aussi être une URL https://...
    val imageUrl: String? = "file:///android_asset/products/$id.jpg",
)

data class CartItem(val product: Product, val quantity: Int) {
    val total: Int get() = product.price * quantity
}

enum class DeliveryMode(val label: String, val detail: String, val fee: Int) {
    HOME("Livraison à domicile", "Livraison sous 1 à 3 jours", 500),
    PICKUP("Retrait en magasin", "Retrait sous 24h", 0),
}

enum class PaymentMode(val label: String) {
    MOBILE_MONEY("Mobile Money"),
    CARD("Carte bancaire"),
}

fun formatFcfa(amount: Int): String =
    amount.toString().reversed().chunked(3).joinToString(" ").reversed() + " FCFA"

object SampleData {
    val categories = listOf(
        Category("cahiers", "Cahiers", "Tout type de cahiers", "📘"),
        Category("stylos", "Stylos", "Stylos, feutres, surligneurs", "🖊️"),
        Category("cartables", "Cartables", "Sacs et accessoires", "🎒"),
        Category("regles", "Règles & Équerres", "Règles, équerres, rapporteurs", "📏"),
        Category("crayons", "Crayons & Coloriages", "Crayons de couleur, feutres", "🖍️"),
        Category("gommes", "Gommes & Taille-crayons", "Gommes, taille-crayons", "🧽"),
        Category("papiers", "Papiers & Intercalaires", "Papiers, intercalaires, protège-cahiers", "📄"),
        Category("accessoires", "Accessoires divers", "Colles, ciseaux, trousses…", "✂️"),
    )

    val products = listOf(
        Product(1, "cahiers", "Cahier 100 pages", "Format A4 - Quadrillé", 350, "📘"),
        Product(2, "cahiers", "Cahier 200 pages", "Format A4 - Séyès", 550, "📕", rating = 4f),
        Product(3, "cahiers", "Cahier grand format", "Format A4 - Quadrillé", 750, "📗"),
        Product(4, "cahiers", "Cahier petit format", "Format 17x22 - Séyès", 300, "📓"),
        Product(5, "cahiers", "Cahier 100 pages (lot de 5)", "Couleurs assorties", 1600, "📚"),
        Product(6, "stylos", "Stylo bille (lot de 4)", "Bleu, noir, rouge, vert", 1200, "🖊️"),
        Product(7, "stylos", "Surligneurs (lot de 4)", "Couleurs fluo", 1500, "🖍️"),
        Product(8, "cartables", "Cartable scolaire", "Collège / Lycée", 8500, "🎒"),
        Product(9, "cartables", "Cartable primaire", "Petit modèle", 6500, "🎒"),
        Product(10, "regles", "Règle 30 cm", "Plastique transparent", 250, "📏"),
        Product(11, "regles", "Set géométrie", "Équerre, rapporteur, règle", 900, "📐"),
        Product(12, "crayons", "Crayons de couleur (12)", "Boîte de 12", 1000, "🖍️"),
        Product(13, "crayons", "Feutres (12)", "Boîte de 12", 1300, "🖌️"),
        Product(14, "gommes", "Gomme + taille-crayon", "Lot de 2", 300, "🧽"),
        Product(15, "papiers", "Intercalaires (lot de 6)", "Format A4", 500, "🗂️"),
        Product(16, "papiers", "Protège-cahiers (lot de 5)", "Couleurs assorties", 400, "📄"),
        Product(17, "accessoires", "Colle bâton", "20 g", 250, "🧴"),
        Product(18, "accessoires", "Ciseaux", "Bouts ronds", 600, "✂️"),
        Product(19, "accessoires", "Trousse", "Double compartiment", 1500, "👝"),
    )

    val popularIds = listOf(1, 6, 2, 8)

    fun byId(id: Int) = products.first { it.id == id }
}
