require: utils.js
    type = scriptEs6
    name = utils

require: text/text.sc
    module = sys.zb-common

theme: /

    state: Start
        q!: $regex</start>
        a: I can encode anything to Base64 and decode it back!
        buttons:
            "Encode"
            "Decode"

    state: Encode
        q!: * encode
        a: Enter a string to be encoded.

        state: EncodeText
            q: *
            q!: * encode $Text
            scriptEs6:
                const encoded = utils.encode($parseTree._Text ?? $parseTree.text);
                $reactions.answer(encoded);
            go!: /AnythingElse

    state: Decode
        q!: * decode
        a: Enter a string to be decoded.

        state: DecodeText
            q: *
            q!: * decode $Text
            scriptEs6:
                const decoded = utils.decode($parseTree._Text ?? $parseTree.text);
                $reactions.answer(decoded);
            go!: /AnythingElse

    state: AnythingElse
        a: Anything else?
        buttons:
            "Encode"
            "Decode"

    state: NoMatch
        event!: noMatch
        a: I’m sorry, I didn’t get it. I can only encode and decode things.
        buttons:
            "Encode"
            "Decode"
