require: s3.js
    type = scriptEs6
    name = s3

require: utils.js
    type = scriptEs6
    name = utils

theme: /

    state: Start
        q!: $regex</start>
        a: I can browse data in S3 buckets.
        go!: /CheckCredentials

    state: CheckCredentials
        scriptEs6:
            if (utils.allSecretsAndVariablesDefined()) {
                s3.initClient();
            } else {
                $reactions.transition("/NoCredentials");
            }
        a: S3 client initialized successfully.
        go!: /ShowBuckets

    state: NoCredentials
        a: To use my features, use the JAICP secrets and variables to store your AWS credentials:
            * Store your access and secret keys in two secrets: “awsAccessKeyId” and “awsSecretAccessKey”.
            * Store the endpoint and region in two environment variables: “endpoint” and “region”.
        a: Deploy them and tell me once you’re done.
        buttons:
            "Done" -> /CheckCredentials

    state: ShowBuckets
        scriptEs6:
            const bucketNames = await s3.getBucketNames();
            delete $session.currentBucket;

            $reactions.answer("Available buckets:");
            bucketNames
                .map((bucketName) => ({ text: bucketName, transition: "/ShowBucketObjects" }))
                .forEach($reactions.buttons);

    state: ShowBucketObjects
        scriptEs6:
            $session.currentBucket = $session.currentBucket ?? $request.query;
            const bucketObjects = await s3.getBucketObjects($session.currentBucket);

            $reactions.answer(`Objects in bucket ${$session.currentBucket}:`);
            bucketObjects
                .map((bucketObject) => ({ text: bucketObject, transition: "/ShowObjectContent" }))
                .concat({ text: "Show buckets", transition: "/ShowBuckets" })
                .forEach($reactions.buttons);

    state: ShowObjectContent
        scriptEs6:
            const currentObject = $request.query;
            const objectContent = await s3.getObjectContent($session.currentBucket, $request.query);

            $reactions.answer(`Contents of object ${currentObject}: ${objectContent}`);
            $reactions.buttons([
                { text: "Show buckets", transition: "/ShowBuckets" },
                { text: "Show bucket objects", transition: "/ShowBucketObjects" },
            ]);

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. I’m but a mere S3 bucket browser.
        if: utils.allSecretsAndVariablesDefined()
            go!: /ShowBuckets
        else:
            go!: /NoCredentials
