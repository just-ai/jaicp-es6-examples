require: js/math.js
    type = scriptEs6
    name = math

require: js/validator.js
    type = scriptEs6
    name = validator

require: js/voca.js
    type = scriptEs6
    name = voca

theme: /

    state: Start
        q!: $regexp</start>
        a: I can show how JAICP bots can utilize various packages.
        a: Which package should I demonstrate?
        buttons:
            "Math"
            "Validator"
            "Voca"

    state: Math
        q!: math
        a: Math.js is an extensive math library. https://mathjs.org/
        a: Send me a math expression, I’ll evaluate it and give you the result! For example:
        buttons:
            "1.2 * (6 + 9)"

        state: Run
            q: *
            scriptEs6:
                try {
                    $reactions.answer(math.evaluate($parseTree.text));
                } catch (err) {
                    $reactions.answer("Sorry, I couldn’t parse that expression.");
                }
            go!: /AnythingElse

    state: Validator
        q!: validator
        a: Validator.js is a library of string validators and sanitizers. https://www.npmjs.com/package/validator
        a: Send me some number sequence and I’ll tell you if it’s a valid postal code.

        state: Run
            q: *
            scriptEs6:
                const result = validator.isPostalCode($parseTree.text, "any");
                $reactions.answer(result ? "This is indeed a postal code." : "Doesn’t look like a postal code to me.");
            go!: /AnythingElse

    state: Voca
        q!: voca
        a: Voca is a library for manipulating strings. https://vocajs.com/
        a: Send me some text and I’ll rewrite it in some random style!

        state: Run
            q: *
            random:
                scriptEs6:
                    $temp = { case: "camel", convertedText: voca.camelCase($parseTree.text) };
                scriptEs6:
                    $temp = { case: "kebab", convertedText: voca.kebabCase($parseTree.text) };
                scriptEs6:
                    $temp = { case: "title", convertedText: voca.titleCase($parseTree.text) };
            scriptEs6:
                $reactions.answer(`In ${$temp.case} case, this would be “${$temp.convertedText}”.`);
            go!: /AnythingElse

    state: AnythingElse
        a: Would you like to try anything else?
        buttons:
            "Math"
            "Validator"
            "Voca"

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. I can only show you how to use some packages.
        buttons:
            "Math"
            "Validator"
            "Voca"
