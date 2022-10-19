require: mongo.js
    type = scriptEs6
    name = mongo

theme: /

    state: Start
        q!: $regex</start>
        a: I can write data to a MondoDB collection and read it back from it.
        go!: /CheckConnectionString

    state: CheckConnectionString
        scriptEs6:
            try {
                mongo.initClient($secrets.get("mongoDbConnectionString")); // Will throw if secret is missing
            } catch (err) {
                $reactions.transition("/NoConnectionString");
            }
        a: MongoDB client initialized successfully.
        a: I will use the {{$injector.dbCollection}} collection in the {{$injector.dbName}} database.
        go!: /Actions

    state: NoConnectionString
        a: To use my features, please configure a secret called “mongoDbConnectionString” in your project.
        a: Use it to store your MongoDB deployment’s connection string.
        a: Tell me once you’re done.
        buttons:
            "Done" -> /CheckConnectionString

    state: Actions
        a: What should I do?
        buttons:
            "Find one element" -> ./FindOne
            "Find all elements" -> ./FindAll
            "Insert element" -> ./Insert

        state: FindOne
            a: What should I search for?

            state: SearchQuery
                q: *
                scriptEs6:
                    const element = await mongo.find($request.query);
                    $reactions.answer(`Here’s what I found: ${JSON.stringify(element)}`);
                go!: /Actions

        state: FindAll
            scriptEs6:
                const elements = await mongo.find();
                $reactions.answer(`Here are the last few elements: ${elements.slice(-5).map(JSON.stringify)}`);
            go!: /Actions

        state: Insert
            a: What should I insert to the database?

            state: InsertQuery
                q: *
                scriptEs6:
                    const insertedId = await mongo.insert($request.query);
                    $reactions.answer(`Element inserted with _id ${insertedId}.`);
                go!: /Actions

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. I can only do simple read/write operations with MongoDB.
        if: $secrets.get("mongoDbConnectionString", null) === null
            go!: /NoConnectionString
        else:
            go!: /Actions
