require: language/languageEn.sc
    module = sys.zb-common

require: openai.js
    type = scriptEs6
    name = openai

require: utils.js
    type = scriptEs6
    name = utils

theme: /

    state: Start
        q!: $regex</start>
        a: Hi, I can translate anything you want using ChatGPT!
        go!: /CheckCredentials

    state: CheckCredentials
        scriptEs6:
            if (utils.allSecretsDefined()) {
                openai.initClient();
            } else {
                $reactions.transition("/NoCredentials");
            }
        a: OpenAI client initialized successfully.
        go!: /RequestLanguage

    state: NoCredentials
        a: To use my features, use the JAICP secrets and variables to store your OpenAI credentials:
            * Store your API key in a secret called “openaiApiKey”.
            * Optionally store the name of the model in an “openaiModel” environment variable. I will use gpt-3.5-turbo by default.
        a: Deploy them and tell me once you’re done.
        buttons:
            "Done" -> /CheckCredentials

    state: RequestLanguage
        a: What language should I translate into?

        state: GetLanguage
            q: * $Language *
            q: * $Language * || fromState = "/RequestText"
            scriptEs6:
                $session.language = $parseTree._Language.name;
            go!: /RequestText

    state: RequestText
        a: What do you need to translate?

        state: GetText
            q: *
            scriptEs6:
                $reactions.answer("Wait a second, I’m translating…");

                const systemPrompt =
                    `I want you to act as a translator into ${$session.language}. ` +
                    `I want you to only reply the translation and nothing else, do not write explanations. ` +
                    `Give the reply in ${$session.language}.`;

                const messages = [
                    {
                        role: "system",
                        content: systemPrompt,
                    },
                    {
                        role: "user",
                        content: $request.query,
                    },
                ];

                openai.gptChatCompletion(messages)
                    .then(({ choices }) => {
                        $conversationApi.sendRepliesToClient([
                            { type: "text", text: choices[0].message.content },
                            { type: "text", text: "Anything else?" },
                        ]);
                    })
                    .catch((e) => {
                        $conversationApi.sendTextToClient("Something went wrong. Please try again later.");
                    });

        state: Waiting || noContext = true
            q: (why ['re/are] you silent/i ['m/am] waiting)
            a: I’m still thinking…

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. All I can do is translate stuff.
        scriptEs6:
            $reactions.transition(utils.allSecretsDefined() ? "/RequestLanguage" : "/NoCredentials");
