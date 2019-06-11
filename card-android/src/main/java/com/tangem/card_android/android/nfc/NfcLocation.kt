package com.tangem.card_android.android.nfc

enum class NfcLocation(val codename: String, val fullName: String, val orientation: Int, val x: Int, val y: Int, val z: Int) {
    model1("sailfish", "Google Pixel", 0, 65, 25, 0),
    model2("walleye", "Google Pixel 2", 0, 40, 15, 0),
    model3("taimen", "Google Pixel 2 XL", 0, 40, 15, 0),
    model4("marlin", "Google Pixel XL", 0, 65, 25, 0),
    model5("blueline", "Google Pixel 3 ", 0, 40, 30, 0),
    model6("crosshatch", "Google Pixel 3 XL", 0, 15, 20, 0),
    model7("htc_pme", "HTC 10 ", 0, 50, 20, 0),
    model8("htc_himau", "HTC One M9", 0, 50, 20, 0),
    model9("htc_himaw", "HTC One M9", 0, 50, 20, 0),
    model10("htc_oce", "HTC U Ultra", 0, 50, 20, 0),
    model11("htc_ocn", "HTC U11", 0, 50, 25, 0),
    model12("htc_ocm", "HTC U11+", 0, 50, 60, 0),
    model13("htc_ocluhljapan", "HTC U11 Life", 1, 50, 25, 0),
    model14("htc_haydugl", "HTC U11 EYEs", 0, 50, 65, 0),
    model15("htc_haydtwl", "HTC U11 EYEs", 0, 50, 65, 0),
    model16("htc_ime", "HTC U12+", 0, 50, 20, 0),
    model17("htc_bre2dugl", "HTC Desire 12s", 0, 50, 10, 0),
    model18("htc_imldugl", "HTC U12 Life", 0, 60, 20, 0),
    model19("htc_exodugl", "HTC EXODUS 1", 0, 50, 20, 0),
    model20("HWFRD", "Huawei Honor 8", 0, 50, 0, 0),
    model21("HWDUK", "Huawei Honor 8 Pro", 0, 50, 0, 0),
    model22("HWSTF", "Huawei Honor 9", 0, 50, 0, 0),
    model23("HWCOL", "Huawei Honor 10", 0, 50, 0, 0),
    model24("HWRVL", "Huawei Honor Note 10", 0, 50, 0, 0),
    model25("HWBKL", "Huawei Honor View 10", 0, 50, 0, 0),
    model26("HWPCT", "Huawei HONOR V20", 0, 60, 20, 0),
    model27("HWTNY", "Huawei Honor Magic 2", 0, 50, 0, 0),
    model28("HWBLN-H", "Huawei Mate 9 Lite / Honor 6X", 0, 40, 0, 0),
    model29("HWALP", "Huawei Mate 10", 0, 50, 0, 0),
    model30("HWBLA", "Huawei Mate 10 Pro", 0, 50, 0, 0),
    model31("HWHMA", "Huawei Mate 20", 0, 50, 20, 0),
    model32("HWEVR", "Huawei Mate 20 X", 0, 50, 20, 0),
    model33("HWLYA", "Huawei Mate 20 Pro", 0, 50, 20, 0),
    model34("angler", "Huawei Nexus 6P", 0, 40, 15, 0),
    model35("hwALE-", "Huawei P8 Lite ", 1, 75, 65, 0),
    model36("HWPRA-H", "Huawei P8 Lite 2017", 0, 50, 25, 0),
    model37("HWEVA", "Huawei P9", 0, 50, 0, 0),
    model38("HWVIE", "Huawei P9 Plus", 0, 50, 0, 0),
    model39("HWVNS-?", "Huawei P9 Lite", 0, 60, 45, 0),
    model40("HWVTR", "Huawei P10", 0, 50, 0, 0),
    model41("HWWAS-H", "Huawei P10 lite", 0, 50, 0, 0),
    model42("HWVKY", "Huawei P10 Plus", 0, 50, 0, 0),
    model43("HWEML", "Huawei P20", 0, 50, 50, 0),
    model44("HWANE", "Huawei P20 Lite", 0, 50, 20, 0),
    model45("HWCLT", "Huawei P20 Pro", 0, 50, 50, 0),
    model46("HW-01K", "Huawei P20 Pro", 0, 50, 50, 0),
    model47("HWELE", "Huawei P30", 0, 50, 50, 0),
    model48("HWVOG", "Huawei P30 Pro", 0, 50, 50, 0),
    model49("HW-02L", "Huawei P30 Pro", 0, 50, 50, 0),
    model50("HWFIG-H", "Huawei P Smart", 0, 45, 0, 0),
    model51("HWPOT-H", "Huawei P smart 2019", 0, 50, 20, 0),
    model52("h1", "LG G5", 0, 30, 20, 0),
    model53("lucye", "LG G6", 0, 50, 50, 0),
    model54("judyln", "LG G7", 0, 50, 50, 0),
    model55("hammerhead", "LG Nexus 5", 0, 50, 50, 0),
    model56("bullhead", "LG Nexus 5X", 0, 45, 15, 0),
    model57("mh", "LG Q6", 1, 25, 50, 0),
    model58("ph2n", "LG Stylo 2+", 0, 75, 20, 0),
    model59("sf340n", "LG Stylo 3+", 0, 75, 20, 0),
    model60("ph1n", "LG Stylus 2", 0, 75, 20, 0),
    model61("msf3", "LG Stylus 3", 0, 75, 20, 0),
    model62("elsa", "LG V20", 0, 50, 15, 0),
    model63("joan", "LG V30", 0, 50, 50, 0),
    model64("L-01K", "LG V30+", 0, 50, 50, 0),
    model65("judyp", "LG V35 ThinQ", 0, 50, 50, 0),
    model66("judyln", "LG G7 ThinQ", 0, 50, 50, 0),
    model67("L-02K", "LG JOJO", 0, 50, 60, 0),
    model68("mcv5a", "LG Q7", 0, 50, 60, 0),
    model69("cv7a", "LG Stylo 4", 0, 50, 60, 0),
    model70("nora_8917_n", "Motorola Moto E5", 0, 50, 40, 0),
    model71("potter_n", "Motorola Moto G5 Plus", 0, 50, 5, 0),
    model72("montana_n", "Motorola Moto G5s", 0, 50, 5, 0),
    model73("sanders_n", "Motorola Moto G5s Plus", 0, 50, 5, 0),
    model74("ali_n", "Motorola Moto G6", 0, 50, 35, 0),
    model75("aljeter_n", "Motorola Moto G6 Play", 0, 50, 35, 0),
    model76("evert_n", "Motorola Moto G6 Plus", 0, 40, 40, 0),
    model77("river_n", "Motorola moto g(7)", 0, 50, 50, 0),
    model78("lake_n", "Motorola moto g(7) plus", 0, 50, 50, 0),
    model79("payton", "Motorola Moto X4", 0, 50, 40, 0),
    model80("nash", "Motorola Moto Z2 Force", 0, 50, 40, 0),
    model81("albus", "Motorola Moto Z2 Play", 0, 50, 5, 0),
    model82("beckham", "Motorola Moto Z3 Play", 0, 50, 0, 0),
    model83("shamu", "Motorola Nexus 6", 0, 50, 60, 0),
    model84("NE1", "Nokia 3", 0, 40, 30, 0),
    model85("ND1", "Nokia 5", 0, 40, 30, 0),
    model86("PLE", "Nokia 6", 0, 50, 20, 0),
    model87("PL2_sprout", "Nokia 6.1", 0, 50, 0, 0),
    model88("C1N", "Nokia 7", 0, 50, 25, 0),
    model89("B2N", "Nokia 7 Plus", 0, 75, 10, 0),
    model90("NB1", "Nokia 8", 0, 50, 30, 0),
    model91("A1N_sprout", "Nokia 8 Sirocco", 0, 90, 0, 0),
    model92("A1N", "Nokia 8 Sirocco", 0, 90, 0, 0),
    model93("OnePlus3", "OnePlus 3", 0, 50, 15, 0),
    model94("OnePlus3T", "OnePlus 3t", 0, 50, 15, 0),
    model95("OnePlus5", "OnePlus 5", 0, 80, 5, 0),
    model96("OnePlus5T", "OnePlus 5t", 0, 80, 5, 0),
    model97("OnePlus6", "OnePlus 6", 0, 50, 20, 0),
    model98("A0001", "OnePlus One", 0, 50, 50, 0),
    model99("OnePlus6T", "OnePlus6T", 0, 50, 20, 0),
    model100("OnePlus6TSingle", "OnePlus6T", 0, 50, 20, 0),
    model101("OnePlus7Pro", "OnePlus 7 Pro", 0, 30, 25, 0),
    model102("OnePlus7ProTMO", "OnePlus 7 Pro", 0, 30, 25, 0),
    model103("a5x", "Samsung Galaxy A5 (2016)", 1, 50, 45, 0),
    model104("a5y17lte", "Samsung Galaxy A5 (2017)", 1, 65, 45, 0),
    model105("a7y17lte", "Samsung Galaxy A7 (2017)", 1, 60, 40, 0),
    model106("a7y18lte", "Samsung Galaxy A7 (2018)", 0, 40, 20, 0),
    model107("a7y18lteks", "Samsung Galaxy A7 (2018)", 0, 40, 20, 0),
    model108("jackpotlte", "Samsung Galaxy A8 (2018)", 1, 50, 50, 0),
    model109("a8", "Samsung Galaxy A8 / A8 (2016)", 0, 50, 15, 0),
    model110("jackpot2lte", "Samsung Galaxy A8+", 1, 50, 45, 0),
    model111("a8sqlte", "Samsung Galaxy A8s", 0, 50, 25, 0),
    model112("a8sqltechn", "Samsung Galaxy A8s", 0, 50, 25, 0),
    model113("a9xltechn", "Samsung Galaxy A9 (2016)", 1, 50, 45, 0),
    model114("a9y18qltekx", "Samsung Galaxy A9 (2018)", 0, 40, 25, 0),
    model115("a9y18qlte", "Samsung Galaxy A9 (2018)", 0, 40, 25, 0),
    model116("c5ltechn", "Samsung Galaxy C5", 0, 45, 15, 0),
    model117("c5pltechn", "Samsung Galaxy C5", 0, 45, 15, 0),
    model118("c5proltechn", "Samsung Galaxy C5 Pro", 1, 45, 15, 0),
    model119("c7ltechn", "Samsung Galaxy C7", 0, 50, 15, 0),
    model120("c7prolte", "Samsung Galaxy C7 Pro", 1, 50, 15, 0),
    model121("c9lte", "Samsung Galaxy C9 Pro", 0, 45, 15, 0),
    model122("j5x", "Samsung Galaxy J5 (2016)", 1, 40, 50, 0),
    model123("j5y17lte", "Samsung Galaxy J5 (2017) / J5 Pro", 1, 60, 50, 0),
    model124("j6lte", "Samsung Galaxy J6", 1, 60, 50, 0),
    model125("j7y17lte", "Samsung Galaxy J7 Pro", 1, 40, 50, 0),
    model126("noblelte", "Samsung Galaxy Note5", 1, 50, 60, 0),
    model127("SCV37", "Samsung Galaxy Note8", 1, 50, 45, 0),
    model128("SC-01K", "Samsung Galaxy Note8", 1, 50, 45, 0),
    model129("great", "Samsung Galaxy Note8", 1, 50, 45, 0),
    model130("crownqle", "Samsung Galaxy Note9", 1, 50, 50, 0),
    model131("crownlte", "Samsung Galaxy Note9", 1, 50, 50, 0),
    model132("SC-01L", "Samsung Galaxy Note9", 1, 50, 50, 0),
    model133("SCV40", "Samsung Galaxy Note9", 1, 50, 50, 0),
    model134("SC-05G", "Samsung Galaxy S6", 1, 30, 60, 0),
    model135("zeroflte", "Samsung Galaxy S6", 1, 30, 60, 0),
    model136("SCV31", "Samsung Galaxy S6 edge", 1, 30, 60, 0),
    model137("404SC", "Samsung Galaxy S6 edge", 1, 30, 60, 0),
    model138("SC-04G", "Samsung Galaxy S6 edge", 1, 30, 60, 0),
    model139("zerolte", "Samsung Galaxy S6 edge", 1, 30, 60, 0),
    model140("zenlte", "Samsung Galaxy S6 edge+", 1, 50, 55, 0),
    model141("heroqlte", "Samsung Galaxy S7", 1, 50, 45, 0),
    model142("herolte", "Samsung Galaxy S7", 1, 50, 45, 0),
    model143("poseidonlteatt", "Samsung Galaxy S7 Active", 1, 45, 55, 0),
    model144("SCV33", "Samsung Galaxy S7 Edge", 1, 50, 45, 0),
    model145("SC-02H", "Samsung Galaxy S7 Edge", 1, 50, 45, 0),
    model146("hero2", "Samsung Galaxy S7 Edge", 1, 50, 45, 0),
    model147("SCV36", "Samsung Galaxy S8", 0, 50, 55, 0),
    model148("SC-02J", "Samsung Galaxy S8", 0, 50, 55, 0),
    model149("dreamlte", "Samsung Galaxy S8", 0, 50, 55, 0),
    model150("dreamqlte", "Samsung Galaxy S8", 0, 50, 55, 0),
    model151("cruiserlte", "Samsung Galaxy S8 Active", 0, 50, 50, 0),
    model152("SCV35", "Samsung Galaxy S8+", 0, 50, 45, 0),
    model153("SC-03J", "Samsung Galaxy S8+", 0, 50, 45, 0),
    model154("dream2", "Samsung Galaxy S8+", 0, 50, 45, 0),
    model155("SCV38", "Samsung Galaxy S9", 0, 50, 50, 0),
    model156("SC-02K", "Samsung Galaxy S9", 0, 50, 50, 0),
    model157("starlte", "Samsung Galaxy S9", 0, 50, 50, 0),
    model158("starqlte", "Samsung Galaxy S9", 0, 50, 50, 0),
    model159("SCV39", "Samsung Galaxy S9+", 0, 50, 60, 0),
    model160("SC-03K", "Samsung Galaxy S9+", 0, 50, 60, 0),
    model161("star2", "Samsung Galaxy S9+", 0, 50, 60, 0),
    model162("beyond1q", "Samsung Galaxy S10", 0, 50, 50, 0),
    model163("beyond1", "Samsung Galaxy S10", 0, 50, 50, 0),
    model164("beyond2q", "Samsung Galaxy S10+", 0, 50, 50, 0),
    model165("beyond2", "Samsung Galaxy S10+", 0, 50, 50, 0),
    model166("beyond0q", "Samsung Galaxy S10e", 0, 50, 50, 0),
    model167("beyond0", "Samsung Galaxy S10e", 0, 50, 50, 0),
    model168("F331", "Sony Xperia E5", 0, 50, 25, 0),
    model169("G33", "Sony Xperia L1", 0, 50, 30, 0),
    model170("H33", "Sony Xperia L2", 0, 50, 20, 0),
    model171("H43", "Sony Xperia L2", 0, 50, 20, 0),
    model172("F512", "Sony Xperia X", 0, 10, 5, 1),
    model173("suzu", "Sony Xperia X", 0, 10, 5, 1),
    model174("F5321", "Sony Xperia X Compact", 0, 50, 55, 0),
    model175("SO-02J", "Sony Xperia X Compact", 0, 50, 55, 0),
    model176("F813", "Sony Xperia X Performance", 0, 10, 5, 1),
    model177("SOV33", "Sony Xperia X Performance", 0, 10, 5, 1),
    model178("SO-04H", "Sony Xperia X Performance", 0, 10, 5, 1),
    model179("502SO", "Sony Xperia X Performance", 0, 10, 5, 1),
    model180("F311", "Sony Xperia XA", 0, 50, 20, 0),
    model181("F321", "Sony Xperia XA Ultra", 0, 50, 25, 0),
    model182("G31", "Sony Xperia XA1", 0, 50, 20, 0),
    model183("G34", "Sony Xperia XA1 Plus", 0, 50, 25, 0),
    model184("G32", "Sony Xperia XA1 Ultra", 0, 50, 20, 0),
    model185("H31", "Sony Xperia XA2", 0, 50, 15, 0),
    model186("H41", "Sony Xperia XA2", 0, 50, 15, 0),
    model187("H4493", "Sony Xperia XA2 Plus", 0, 50, 20, 0),
    model188("H4413", "Sony Xperia XA2 Plus", 0, 50, 20, 0),
    model189("H3413", "Sony Xperia XA2 Plus", 0, 50, 20, 0),
    model190("H32", "Sony Xperia XA2 Ultra", 0, 50, 20, 0),
    model191("H42", "Sony Xperia XA2 Ultra", 0, 50, 20, 0),
    model192("F833", "Sony Xperia XZ", 0, 20, 5, 1),
    model193("SO-01J", "Sony Xperia XZ", 0, 20, 5, 1),
    model194("601SO", "Sony Xperia XZ", 0, 20, 5, 1),
    model195("SOV34", "Sony Xperia XZ", 0, 20, 5, 1),
    model196("G81", "Sony Xperia XZ Premium", 0, 50, 55, 0),
    model197("SO-04J", "Sony Xperia XZ Premium", 0, 50, 55, 0),
    model198("G834", "Sony Xperia XZ1", 0, 45, 5, 0),
    model199("701SO", "Sony Xperia XZ1", 0, 45, 5, 0),
    model200("SOV36", "Sony Xperia XZ1", 0, 45, 5, 0),
    model201("SO-01K", "Sony Xperia XZ1", 0, 45, 5, 0),
    model202("G8441", "Sony Xperia XZ1 Compact", 0, 50, 30, 0),
    model203("SO-02K", "Sony Xperia XZ1 Compact", 0, 50, 30, 0),
    model204("H82", "Sony Xperia XZ2", 0, 20, 40, 0),
    model205("SOV37", "Sony Xperia XZ2", 0, 20, 40, 0),
    model206("SO-03K", "Sony Xperia XZ2", 0, 20, 40, 0),
    model207("702SO", "Sony Xperia XZ2", 0, 20, 40, 0),
    model208("H8166", "Sony Xperia XZ2 Premium", 0, 60, 20, 0),
    model209("SO-04K", "Sony Xperia XZ2 Premium", 0, 60, 20, 0),
    model210("SOV38", "Sony Xperia XZ2 Premium", 0, 60, 20, 0),
    model211("H8116", "Sony Xperia XZ2 Premium", 0, 60, 20, 0),
    model212("H8324", "Sony Xperia XZ2 Compact", 0, 50, 25, 0),
    model213("SO-05K", "Sony Xperia XZ2 Compact", 0, 50, 25, 0),
    model214("H8314", "Sony Xperia XZ2 Compact", 0, 50, 25, 0),
    model215("G823", "Sony Xperia XZs", 0, 20, 5, 1),
    model216("602SO", "Sony Xperia XZs", 0, 20, 5, 1),
    model217("SOV35", "Sony Xperia XZs", 0, 20, 5, 1),
    model218("SO-03J", "Sony Xperia XZs", 0, 20, 5, 1),
    model219("801SO", "Sony Xperia XZ3", 0, 60, 20, 0),
    model220("H9493", "Sony Xperia XZ3", 0, 60, 20, 0),
    model221("H9436", "Sony Xperia XZ3", 0, 60, 20, 0),
    model222("SOV39", "Sony Xperia XZ3", 0, 60, 20, 0),
    model223("SO-01L", "Sony Xperia XZ3", 0, 60, 20, 0),
    model224("H8416", "Sony Xperia XZ3", 0, 60, 20, 0),
    model225("gemini", "Xiaomi Mi 5", 0, 40, 20, 0),
    model226("capricorn", "Xiaomi Mi 5S", 0, 50, 10, 0),
    model227("natrium", "Xiaomi Mi 5S Plus", 0, 45, 15, 0),
    model228("sagit", "Xiaomi Mi 6", 0, 50, 20, 0),
    model229("dipper", "Xiaomi Mi 8", 0, 45, 20, 0),
    model230("ursa", "Xiaomi MI 8 Explorer Edition", 0, 50, 40, 0),
    model231("cepheus", "Xiaomi MI 9", 0, 40, 20, 0),
    model232("grus", "Xiaomi MI 9 SE", 0, 40, 20, 0),
    model233("lithium", "Xiaomi Mi MIX", 0, 20, 20, 0),
    model234("chiron", "Xiaomi Mi MIX 2", 0, 45, 20, 0),
    model235("polaris", "Xiaomi Mi MIX 2S", 0, 45, 20, 0),
    model236("scorpio", "Xiaomi Mi Note 2", 0, 50, 20, 0),
    model237("jason", "Xiaomi Mi Note 3", 0, 50, 20, 0),
    model238("perseus", "Xiaomi MIX 3", 0, 60, 20, 0),
    model239("bbb100", "BlackBerry KEYone", 0, 60, 20, 0),
    model240("bbf100", "BlackBerry KEY2", 0, 50, 30, 0),
    model241("CatS61", "Cat S61", 0, 50, 50, 0),
}