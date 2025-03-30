package rockland.elysiancrest.com.data_service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageDownloader {

    private static final String URL_PREFIX_TO_IGNORE = "https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/";
    private static final String ORIGINAL_FOLDER = "original_images";
    private static final String THUMBNAIL_FOLDER = "thumbnails";
    private static final long THUMBNAIL_SIZE_LIMIT = 200 * 1024; // 200kB

    public static void main(String[] args) {
        // List of image URLs
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/20-BARREL-CON-SUR-PINOT-NOIR-750-ML-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VIN-MOUSSEUX-A-NOS-AMURS-BRUT-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VIN-MOUSSEUX-A-NOS-AMURS-BRUT-750-ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUT-RASPBERRY-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUT-VODKA-PEPPAR-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUTE-VODKA-MANDERIN-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUTE-VODKA-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUTE-VODKA-CITRON-750-ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ABSOLUTE-VODKA-VANILA-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ALTEMURA-APULO-SALENTO-FIANO-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ALTEMURA-APULO-SALENTO-PRIMITIVO-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/AMARONE-DELLA-VALPOLICELLA-DOC-CLASSICO-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/angel-vodka-rockland.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ANTINORI-SAN-GIOVANNI-ORVIETO-CLASSICO-75CL.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SANTA-CRISTINA-GIARDINO-ROSE-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ANTINORI-TIGNANELLO-TOSCANA-ROSSO-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VILLA-ANTINORI-BIANCO-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VILLA-ANTINORI-TOSCANA-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/Aperol.jpg.webp");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/ARTISAN-MEZCAL-MONTE-LUNA-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/AUCHENTOSHAN-SINGLE-MALT-SCOTCH-WHISKY.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ballantines-finest-whisky.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BALVENIE-12-YRS-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BALVENIE-17-YRS-700ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/Balvenie-21-years-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BANROCK-STATION-CHARDONNAY-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BANROCK-STATION-SAUVIGNON-BLANC-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/BANROCK-STATION-SHIRAZ-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BARSOL-PISCO-QUEBRANTA-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/beefeater-london-dry-gin.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELL-HOMESTEAD-CHARDONNAY-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bellingham-homestead-orch-chen-blanc.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bellingham-homestead-sauvignon-blanc.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELL-HOMESTEAD-SHIRAZ-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELLERUCHE-BLANC-COTESDU-RHONE-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELUGA-NOBLE-VODKA-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELVEDERE-VODKA-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BER.SERIES-CHENIN-BLANC-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bernard-series-bush-vine-pinotage.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bernard-series-pressed-basket-syrah.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BISON-STRONG-CAN-330-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BISON-SUPER-STORNG-CAN-500-ML-scaled-1.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BOGARTS-BITTERS-350-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BOUCHARD-AINE-FILS-CHABLIS-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bouchard-a_n_-_-fils-macon-superior-rouge.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BOUCHARD-AINE-FILS-MACON-VILLAGES-CHARDONNAY-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BAF-POUILLY-FUISSE.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BOUCHARD-AINE-FILS-VIN-DE-PAYS-CHARDONNAY-750ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BOWMORE-12-YEARS-SCOTCH-WHISKY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BRANCAMENTA-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BROKERS-GIN-50ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BROKERS-LONDON-DRY-GIN-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BROKERS-PINK-GIN-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CANADIAN-CLUB-5-YRS-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CAPTAIN-MORGAN-SPICED-GOLD-RUM.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CARLSBERG-PREM-SMOOTH-DRAUGHT-500ML-CAN-1-scaled-1.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CARLSBERG-PILSNER-330-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CARLSBERG-PREMIUM-PILSNER-500-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CARLSBERG-SPECIAL-BREW-500ML-CAN-scaled-1.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CASCO-VIEJO-TIQUILA-SILVER-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CELERY-BITTERS-200ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CEYLON-ARRACK-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CEYLON-ARRACK-SPECIAL-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHATEAU-CLARKE-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHATEAU-DES-FERRAGES-NOTE-ROSE-IGP-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHATEAU-DES-LAURETS-BARON-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHATEAU-MIRAVAL-PROVINCE-ROSE-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHIVAS-REGAL-12YO-1000ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHIVAS-REGAL-12YO-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHIVAS-REGAL-18YO-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/btt-rosso.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ciroc-vodka.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/charles-de-f_re-jean-louis-blanc-de-blancs-brut.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/charles-de-f_re-jean-louis-blanc-de-blancs-demi-sec.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/oip_11_.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COINTREAU-50-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COINTREAU-LIQEUOR-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COLOMBO-GIN-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COLOMBO-GIN-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/COLOMBO-GIN-500ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/colombo-8-gin.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/3.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/4.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/5.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/6.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/7.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/06/8.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-BICI-RES-CAB-SAUVI-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-BICI-RES-CHARDONNAY-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-BICI-RES-MERLOT-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-BICI-RES-SAUVIG-BLANC.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/cono_sur_20_barrels_cab_sauv.png");


        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-ORG-CAB-SAU-CARMENERE-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/cono-sur-organic-sauvignon-blanc.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/aq29py0mqci1ive-tyqsrq_pb_600x600.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/cono-sur-reserva-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-RES-MERLOT-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-RES-SAUVIGNON-BLANC-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-SAUVIGNON-BLANC-750ML-20-BARREL.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/untitled-1.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/cono-sur-tocornal-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-TOC-MERLOT-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CONO-SUR-TOC-SAUVIGNON-BLANC-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COTES-DE-PROVENCE-ROSE-ROUMERY-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/COURVOSIER-VS-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CROFT-FINE-WHITE-PORT-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CROFT-PINK-PORT.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CRYSTAL-HEAD-VODKA-50-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CRYSTAL-HEAD-VODKA-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CULEMBORG-CAPE-WHITE-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CULEMBORG-SWEET-RED-750ML-X-6-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/DOCKYARD-GIN-500ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/DOLIN-VERMOUTH-DE-CHAMBERY-BLANC-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/DOLIN-VERMOUTH-DE-CHAMBERY-DRY-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/DOUBLE-DISTILLED-ARRACK-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/DOUBLE-DISTILLED-ARRACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/DR.L-RIESLING-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/el_zorro_bianco.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/EL-ZORRO-EL-ROJO-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/ROCKLAND-ES-ARRACK-375-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ES-EXTRA-SPECIAL-ARRACK-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/ROCKLAND-ES-ARRACK-NATURAL-SPEICY-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ES-NATURAL-SPEICY-34-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FAMILLE-COTES-DU-RHONE-RESERVE-ROUGE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FAMILLE-PERRIN-CHATEAUNEUF-DU-PAPE-LES-SINARDS-ROUGE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/untitled-2_1_.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FINLANDIA-VODKA-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FLECHAS-DE-LOS-ANDES-GRAN-CORTE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FLECHAS-DE-LOS-ANDES-GRAN-MALBEC-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FLECHAS-DE-LOSAND-PUNTA-DE-BLEND-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FOUR-PILLARS-BLODDY-SHIRAZ-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/FOUR-PILLARS-RARE-DRY-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GARZON-ALBARINO-RESERVA-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GARZON-PINOT-ROSE-DE-CORTE-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GARZON-TANNAT-DE-CORTE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GARZON-VIOGNIER-DE-CORTE-750ML-3.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLEN-SCOTIA-15YRS-SINGLE-MALT.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/Glenfarclas-10-Year-Old-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MicrosoftTeams-image-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/Glenfarclas-15-Year-Old-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-12-YRS-50-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-12YRS-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-15-YRS-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-15-YRS-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-18-YRS-700-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-21-YRS-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-IPA-700ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GLENFIDDICH-XX-700ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GORDON-S-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GOVENORS-CHOICE-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GOVENORS-CHOICE-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GRANTS-TRIPLE-WOOD-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/GRAPPA-CLASSICA-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HALMILLA-OLD-ARRACK-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HALMILLA-OLD-ARRACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/HANAPPIER-BRANDY-375ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HANAPPIER-BRANDY-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/HANAPPIER-FRENCH-TRADITIONAL-BRANDY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/hardys-hrb-chardonnay-new.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HARDYS-STAMP-CABERNET-MERLOT.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HARDY-STAMP-CHARDONNAY-SEMILLON-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HARDYS-STAMP-SEMILLON-SAUVIGNON-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HARDYS-STAMP-SHIRAZ-CABERNET-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HAYMANS-LONDON-DRY-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HAYMANS-SOLE-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HEINEKEN-BEER-CAN-330-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HEINEKEN-BEER-CAN-500ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HENDRICK-GIN-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/HENDRICKS-GIN-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HENNESSY-VS-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HENNESSY-VSOP-700-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/HENNESSY-XO-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/hugel-gewurztraminer.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JB-RARE-SCOTCH-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JACK-DANIELS-BLACK-1000ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JAGERMEISTER-750-ML.png");


        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JAM-JAR-SWEET-BLUSH-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/JAM-JAR-SWEET-SHIRAZ-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JAM-JAR-SWEET-WHITE-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JERRY-THOMAS-OWN-DECANTER-BITTERS-200-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JIM-BEAM-4Y-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/j.-moreau-_-fils-rose-d_anjou.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHN-JAMESON-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHNNIE-WALKER-BLACK-LABEL-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHNNIE-WALKER-BLUE-LABEL-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHNNIE-WALKER-DOUBLE-BLACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHNNIE-WALKER-GOLD-RESERVE-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOHNNIE-WALKER-RED-LABEL-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOSEPH-DROUHIN-BEAUJOLAIS-VILLAGES-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/joseph-drouhin-chablis-premier-cru.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/JOSEPH-DROUHIN-NUITS-SAINT-GEORGES-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KAVALAN-CLASSIC-SINGLE-MALT-WHISKY-700ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KAVALAN-PODIUM-SINGLE-MALT-WHISKY-700ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KEROFF-VODKA-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KEROFF-VODKA-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KINCARDEN-BLENDED-SCOTCH-WHISKY-1000-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/KINCARDEN-BLENDED-SCOTCH-WHISKY-700-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/KUMALA-SAUVIGNON-BLANC-COLOMBARD-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LAPHROAIG-10YRS-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LAPHROAIG-SELECT-40-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LB-CHATEAUNEUF-DU-PAPE-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LEMON-BITTERS-200ML-3.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LES-GRANGES-HAUT-MEDOG-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-ICE-330-ML-CAN.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/03/2750_1_pr.jpeg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-LAGER-BEER-330ML-CAN-1-2.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-LAGER-BEER-500ML-CAN-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-STOUT-330ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-STOUT-BEER-500ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-STRONG-330ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LION-STRONG-BEER-500ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/LOCH-LOMAND-ORIGINAL-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LOCH-LOMOND-SIN.-GRAIN-WHISKY.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LB-COTES-DU-RHONE-RED-750ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LUCKY-FISH-TODDY-CAN-500ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/lugana-doc-san-bendetto.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LUXARDO-APERITIVO-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LUXARDO-BITTER-BIANCO-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/LUXARDO-LIMONCELLO-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/maraschino-luxardo.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/makers-mark.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MARIUS-IGP-BLANC-WHITE.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MARIUS-IGP-ROSE.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MARIUS-RED.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MARTIN-MILLERS-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/crop-3.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MIDORI-MELON-LIQUEUR-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MIGNON-GRANDE-RESERVE-BRUT-750-ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MIGNON-ROSE-BRUT-PIERRE-750-ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CAM-CABERNET-SAUVIGNON-750-ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CAM-MERLOT-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/MILLAMAN-CAM-SAUVIGNON-BLANC-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CON-CABERNET-SAUVIGNON-375-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CON-CABERNET-SAUVIGNON-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CON-CHARDONNAY-1500ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/millaman-condor-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CON-SAUVIGNON-BLANC-375-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/millaman-condor-sauvignon-blanc.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-CON-MERLOT-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MILLAMAN-EST.RES_.CHARDONNAY-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/millaman-estate-reserva-sauvignon-blanc.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MONKEY-SHOULDER-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MONKEY-SHOULDER-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/mount-gay-xo-776x1176-1.jpeg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CASTELLO-DELLA-SALA-MUFFATO-DELLA-SALA.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/NAPOLEAN-BRANDY-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/NAPOLEON-BRANDY-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/NAVY-SEAL-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/NAVY-SEAL-WHITE-ARRACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/oip_19_.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/BELUGA-NOBLE-VODKA-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/OLD-KEG-WHISKY-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OLD-KEG-WHISKY-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/OLD-KEG-DOUBLE-BLEND-WHISKY-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OLD-KEG-WHISKY-DOUBLE-BLEND-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OLIVE-BITTERS-200ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ORANGE-BITTERS-200-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OYSTER-BAY-CHARDONNAY-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OYSTER-BAY-PINOT-NOIR-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/OYSTER-BAY-SAUVIGNON-BLANC-750ml-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PARADISE-WHITE-COCONUT-ARRACK-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bin-2-shiraz-mataro.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bin-28-shiraz.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/bin-389-cabernet-shiraz.jpg");

        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PENFOLDS-KOONUNGA-HILL-76-SHIRAZ-CABERNET-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/koonunga-hill-autumn-riesling.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/koonunga-hill-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PENFOLDS-KOONUNGA-HILL-SHIRAZ-CABERNET-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ST.HENRI-SHIRAZ-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PEPPOLI-CHIANTI-CLASSICO-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/PHILIPPE-RAGUENOT-CREMANT-BLANC-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/CREMANT-BLANC-PHILIPPE-RAGUENOT-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PIAN-DELLE-VIGNE-BRUNELLO-DI-MONTALCINO.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/POL-ROGER-RESERVE-BRUT.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/POL-ROGER-CUVEE-SIR-WINSTON-CHURCHILL.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/POL-ROGER-VINTAGE-750ML-3.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/POMMERY-APANAGE-BLANC-DE-BLANC-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/POMMERY-BRUT-ROSE-ROYAL-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/POMMERY-BRUT-ROYAL-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/POMMERY-GRAN-CRU-ROYAL-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/PROCERA-GIN-500ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/punt-road-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/punt-road-pinot-gris.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/04/PUNT-ROAD-VALLEY-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/punt-road-shiraz-domestic.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/REMY-MARTIN-VSOP-50-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/REMY-MARTIN-VSOP-750ML-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/REMY-MARTIN-XO-SPECIAL-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RESERVA-AMARONE-VALPOLICELLA-DOC-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RH-3-ETOILES-PLANTATION-700ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RH-DARK-ORIGINAL-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RIMAPERE-SAUVIGNON-BLANC-MARLBOR-750ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ripassa-valpolicella-doc-sup.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROBERT-GIRAUD-LA-COLLECTION-BORDEAUX-BLANC-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/RG-LA-COLLECTION-BORDX-ROUGE-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/robert-mondavi-woodbridge-lightly-oaked-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROBERT-MONDAVI-NAPA-VALLEY-CABERNET-SAUVIGNON-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROBERT-MONDAVI-NAPA-VALLEY-FUME-BLANC-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROBERT-MONDAVI-PRIVATE-SELECTION-ZINFANDEL-750-ML-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROBERT-MONDAVI-WHITE-ZINFANDEL-750-ML-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-DARK-RED-RUM-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-DARK-RED-RUM-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/ROCKLAND-DRY-GIN-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-DRY-GIN-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-LEMON-GIN-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-LEMON-GIN-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-EXTRA-STRONG-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-EXTRA-STRONG-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/ROCKLAND-OLD-ARRACK-375-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/12/ROCKLAND-OLD-ARRACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/01/ROCKLAND-THREE-STAR-BLACK-LABEL-ARRACK-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-WHITE-RUM-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROCKLAND-WHITE-RUM-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROKU-GIN-700ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROSKAA-VODKA-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROSKAA-VODKA-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ROTHBURY-SHIRAZ-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RUFFINO-CHIANTI-D.O.C.G-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ruffino-orvieto-classico-d.o.c.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RUPERT-ROTHSCHILD-BARONE-NADINE-CHARDONNAY-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RUPERT-ROTHSCHILD-CLASSIQUE-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/CHATEAU-DES-FERRAGES-NOTE-ROSE-IGP-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RUSSIAN-STANDARD-ORIGINAL-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RYDERS-GINGER-BLAST-500-ML-CAN-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/RYDERS-500-ML-CANS.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/sailor-jerry-rum.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/saint-joseph-deschants-rouge.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SAINT-JOSEPH-LES-GRANILITES-AOC-750ML-2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/sambuca-dei-cesari.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SANTA-CRISTIANA-ROSSO-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/santa-julia-organic-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SANTA-JULIA-ORGANICA-MALBEC.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SANTA-JULIA-RESERVA-MALBEC.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/santa-margherita-chinathi-classico.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/cq5dam.web_.1280.1280_1_.jpeg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/maremma-toscana-sangiovese-sassoregale-1833169-s43_e.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SCALABRONE-TENUTA-GUADO-AL-TASSO-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SIPSMITH-LONDON-DRY-GIN-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SLOE-GIN-500-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SMIRONOFF-RED-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SOMERSBY-APPLE-BEER-500-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SOMERSBY-BLACKBERRY-500ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/spy-valley-pinot-noir.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/SPY-VALLEY-SAUVIGNON-BLANC-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ST-REMY-BRANDY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ST.REMY-BRANDY-XO-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/MIRAVAL-STUDIO-BY-MIRAVAL-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/SUNKISSED-SWEET-RED-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/SUNKISSED-SWEET-ROSE-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/SUNKISSED-SWEET-WHITE-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALISKER-10-YEAR-OLD-WHISKY.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/TALL-HORSE-BERRY-SWEET-RED-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-CAB-SAUVIGNON-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-CHARDONNAY-750ML-1.png");

        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-CHENIN-BLANC-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-MERLOT-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/TALL-HORSE-MOSCATO-750ML-.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-PINOTAGE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-PINOTAGE-ROSE-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-SAUVIGNON-BLANC-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TALL-HORSE-SHIRAZ-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TAMRAS-INDIAN-DRY-GIN-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TANQUAREY-LONDON-DRY-GIN-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/taylors-fine-tawny.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/taylor_s-first-estate-reserve-port.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/taylors-lbv.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/taylors-select-res_1.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TEQUILA-ROSE-STRAWBERRY-CREAM-LIQUEUR-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/THE-BEACH-HOUSE-ROSE-WINE-750-ML-1-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/THE-BEACH-HOUSE-SAUVIGNON-BLANC-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2024/05/TIGER-BLACK-500-ML-CAN.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRES-FLORALIS-MOSCATEL-ORO-VINO-DE-LICOR.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRES-GRAN-VINA-SOL-CHA.-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRES-MAS-LA-PLANA.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/st_bot.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ve_bot.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/vs_bot.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRESELLA-PINOT-GRIGIO-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/torrs-prosecco-brut-spumante.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRESELLA-REFOSCO-ROSSO-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TORRRES-SANGRE-DE-TORO-187.50ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TWIST-ENGLISH-APPLE-VODKA-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TWIST-EXOTIC-LYCHEE-VODKA-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TWIST-ORIGINAL-VODKA-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TWIST-RASBERRY-BURST-VODKA-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/TWIST-SPANISH-LEMON-VODKA-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/URAKASUMI-SAKE-JUNMAISHU-1800-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/URAKASUMI-SAKE-JUNMAISHU-720-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/URAKASUMI-SAKE-KIIPPON-180-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/URAKASUMI-SAKE-KIIPPON-1800-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/URAKASUMI-SAKE-KIIPPON-720-ML-3-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VALPOLICELLA-DOC-SUP-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VASSE-FELIX-CABERNET-MERLOT-750-ML-3-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VASSE-FELIX-CABERNET-SAUVIGNON-750-ML-2-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VASSE-FELIX-CHARDONNAY-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VASSE-FELIX-SAUVIGNON-BLANC-SEMILLON-750-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VAT-69-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VAT-9-ORG.-FAMILY-RESERVE-375-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VAT-9-ORG.-FAMILY-RESERVE-WITH-BOX-750-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VAT-9-ORG.-FAMILY-RESERVE-WITHOUT-BOX-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VELHO-BARREIRO-TRADITIONAL-39.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/461967011_0_640x640.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/dr.-loosen-villa-wolf-gewurztraminer.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VX-ARRACK-375ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/VX-ARRACK-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-APRICOT-BRANDY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-BLACKBERRY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-BLUE-CURACAO-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CHERRY-BRANDY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CINAMON-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CREAM-DE-CASSIS-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CREME-DE-BANANES-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CREME-DE-CACAO-BROWN-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-CREME-DE-MENTHE-GREEN-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-PEACH-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-RASPBERRY-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-SOUR-APPLE-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-STRAWBERRY-700-ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WENNEKER-TRIPLE-SEC-700-ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/WHITE-LAKE-VODKA-700ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/yalumba-organic-series-a-chardonnay.jpg");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-FALANGHINA-SALENTO-IGT-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/new-prosecco-no-points_2.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-SASSEO-PRIMI-SALENTO-IGT-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-SPARKLING-ROSE-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-VALPOLICELLA-RIPASSO-DOC-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-VENTITERRE-CHIANTI-DOCG-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-VENTITERRE-PINOT-GRIGIO-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-VENTITERRE-SOAVE-DOC-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZONIN-VENTITERRE-VALPOLICELLA-750ML-1.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/ZUCCARDI-SERIEA-CABERNET-SAUVIGNON-750ML.png");
        imageUrls.add("https://wineworldstg.wpengine.com/wp-content/uploads/2023/11/zuccardi-serie-a-torrontes.jpg");




        createDirectories();

        for (String imageUrl : imageUrls) {
            try {
                // Get the filename by removing the prefix
                String fileName = extractFileName(imageUrl);
                String originalPath = ORIGINAL_FOLDER + File.separator + fileName;
                String thumbnailPath = THUMBNAIL_FOLDER + File.separator + fileName;

                // Download the image
                System.out.println("Downloading: " + fileName);
                downloadImage(imageUrl, originalPath);

                // Create a thumbnail
                System.out.println("Creating thumbnail for: " + fileName);
                createThumbnail(originalPath, thumbnailPath);

            } catch (Exception e) {
                System.err.println("Error processing URL: " + imageUrl);
                e.printStackTrace();
            }
        }

        System.out.println("All images processed.");
    }

    private static void createDirectories() {
        new File(ORIGINAL_FOLDER).mkdirs();
        new File(THUMBNAIL_FOLDER).mkdirs();
    }

    private static void downloadImage(String imageUrl, String savePath) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void createThumbnail(String originalPath, String thumbnailPath) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(originalPath));
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Scale down maintaining the aspect ratio
        int scaledWidth = width / 2;
        int scaledHeight = height / 2;
        BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);

        File thumbnailFile = new File(thumbnailPath);
        int quality = 90; // Starting quality

        while (true) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpeg", baos);
            if (baos.size() <= THUMBNAIL_SIZE_LIMIT || quality <= 10) {
                try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
                    fos.write(baos.toByteArray());
                }
                break;
            }
            quality -= 10;
        }
    }

    private static String extractFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}

