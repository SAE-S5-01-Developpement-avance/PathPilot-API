db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE);

db.createUser({
    user: process.env.MONGO_USER,
    pwd: process.env.MONGO_PASSWORD,
    roles: [
        {
            role: "readWrite",
            db: process.env.MONGO_INITDB_DATABASE,
        },
    ],
});

db.mongoClient.createIndex({ location: "2dsphere" });

print("User created successfully for database: " + process.env.MONGO_INITDB_DATABASE);