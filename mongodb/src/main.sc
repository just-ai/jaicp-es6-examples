require: mongo.js
    type = scriptEs6
    name = mongo

require: utils.js
    type = scriptEs6
    name = utils

theme: /

    state: Start
        q!: $regex</start>
        a: I can write data to a MondoDB collection and read it back.
        go!: /CheckCredentials

    state: CheckCredentials
        scriptEs6:
            if (utils.allSecretsAndVariablesDefined()) {
                mongo.initClient();
            } else {
                $reactions.transition("/NoCredentials");
            }
        a: MongoDB client initialized successfully.
        go!: /Actions

    state: NoCredentials
        a: To use my features, use the JAICP secrets and variables to store your MongoDB credentials:
            * Store your deployment’s connection string in a secret called “mongoDbConnectionString”.
            * Store the database and collection names in two environment variables: “mongoDbName” and “mongoDbCollection”.
        a: Deploy them and tell me once you’re done.
        buttons:
            "Done" -> /CheckCredentials

    state: Actions
        a: What should I do?
        buttons:
            "Find one document" -> ./FindOne
            "Find all documents" -> ./FindAll
            "Add document" -> ./Insert

        state: FindOne
            a: Tell me the query to search the document by.

            state: SearchQuery
                q: *
                scriptEs6:
                    const document = await mongo.find($request.query);
                    $reactions.answer(`Here’s what I found: ${JSON.stringify(document)}`);
                go!: /Actions

        state: FindAll
            scriptEs6:
                const documents = await mongo.find();
                $reactions.answer(`Here are the last few documents: ${documents.slice(-5).map(JSON.stringify)}`);
            go!: /Actions

        state: Insert
            a: Send me some random text. I’ll use it as the “query” property value of the document.

            state: InsertQuery
                q: *
                scriptEs6:
                    const insertedId = await mongo.insert($request.query);
                    $reactions.answer(`Document added with _id ${insertedId}.`);
                go!: /Actions

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. I can only do simple read/write operations with MongoDB.
        scriptEs6:
            $reactions.transition(utils.allSecretsAndVariablesDefined() ? "/Actions" : "/NoCredentials");
