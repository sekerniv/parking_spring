const admin = require("firebase-admin");
const geofire = require("geofire-common");
const serviceAccount = require("../src/main/resources/serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

const parkingLots = [
    {
        "id": "dovnov",
        "flatRate": {
            "price": 30,
            "times": [
                {
                    "fromDay": 1,
                    "fromHour": 18,
                    "toDay": 2,
                    "toHour": 7
                },
                {
                    "fromDay": 2,
                    "fromHour": 18,
                    "toDay": 3,
                    "toHour": 7
                },
                {
                    "fromDay": 3,
                    "fromHour": 18,
                    "toDay": 4,
                    "toHour": 7
                },
                {
                    "fromDay": 4,
                    "fromHour": 18,
                    "toDay": 5,
                    "toHour": 7
                },
                {
                    "fromDay": 5,
                    "fromHour": 18,
                    "toDay": 6,
                    "toHour": 7
                },
                {
                    "fromDay": 6,
                    "fromHour": 18,
                    "toDay": 1,
                    "toHour": 7
                }
            ]
        },
        "hourly": {
            "hourPrice": 20
        },
        "name": "דובנוב",
        "residentDiscount": 0.75,
        "address": "דובנוב 7",
        lat: 32.0740802,
        lng: 34.7838054
    }, 
    {
        "id": "golda",
        "flatRate": {
            "price": 30,
            "times": [
                {
                    "fromDay": 1,
                    "fromHour": 18,
                    "toDay": 2,
                    "toHour": 7
                },
                {
                    "fromDay": 2,
                    "fromHour": 18,
                    "toDay": 3,
                    "toHour": 7
                },
                {
                    "fromDay": 3,
                    "fromHour": 18,
                    "toDay": 4,
                    "toHour": 7
                },
                {
                    "fromDay": 4,
                    "fromHour": 18,
                    "toDay": 5,
                    "toHour": 7
                },
                {
                    "fromDay": 5,
                    "fromHour": 18,
                    "toDay": 6,
                    "toHour": 7
                },
                {
                    "fromDay": 6,
                    "fromHour": 1,
                    "toDay": 1,
                    "toHour": 7
                }
            ]
        },
        "hourly": {
            "hourPrice": 20
        },
        "name": "גולדה",
        "residentDiscount": 0.5,
        "address": "ברקוביץ 7",
        lat: 32.0776702,
        lng: 34.7857862
        
    },
    {
        "id": "tel-nordau",
        "flatRate": {
          "price": 30,
          "times": [
            { "fromDay": 1, "fromHour": 19, "toDay": 2, "toHour": 7 },
            { "fromDay": 2, "fromHour": 19, "toDay": 3, "toHour": 7 },
            { "fromDay": 3, "fromHour": 19, "toDay": 4, "toHour": 7 },
            { "fromDay": 4, "fromHour": 19, "toDay": 5, "toHour": 7 },
            { "fromDay": 5, "fromHour": 19, "toDay": 6, "toHour": 7 },
            { "fromDay": 6, "fromHour": 7, "toDay": 7, "toHour": 7 },
            { "fromDay": 7, "fromHour": 7, "toDay": 1, "toHour": 7 },
          ]
        },
        "hourly": {
          "hourPrice": 20
        },
        "name": "תל נורדאו",
        "residentDiscount": 0.75,
        "address": "פרישמן 28",
        "lat": 32.0894,
        "lng": 34.7740,

      },
      

];

async function importLots() {
  for (const lot of parkingLots) {
    const { id, lat, lng, ...rest } = lot;

    await db.collection("parkingLots").doc(id).set({
      ...rest,
      location: new admin.firestore.GeoPoint(lat, lng),
      geohash: geofire.geohashForLocation([lat, lng])
    });

    console.log(`✅ Imported: ${lot.name}`);
  }
}

importLots();

importLots();