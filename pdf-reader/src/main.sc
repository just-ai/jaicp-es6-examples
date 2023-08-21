require: pdf-reader.js
    type = scriptEs6
    name = pdfReader

require: paragraph-builder.js
    type = scriptEs6
    name = paragraphBuilder

theme: /

    state: Start
        q!: $regex</start>
        a: Hi! I can read PDF files for you. Just send me one and see for yourself!

    state: AcceptFile
        event!: fileEvent
        scriptEs6:
            // Here we clear the temporary file directory to avoid exceeding the storage quota,
            // as well as possible name collisions with any files previously uploaded by this user.
            await $storage.clear();
            const fileObject = $request.data.eventData[0];

            if (fileObject.type !== "file" || fileObject.mimeType !== "application/pdf") {
                $reactions.answer("I was designed to only accept PDF files, sorry. Try another one!");
                return;
            }

            try {
                $session.filePath = await pdfReader.downloadPdf(fileObject.url, $request.channelUserId);
                $session.currentPage = 0;
                $reactions.transition("/PaginateFile");
            } catch (e) {
                $reactions.answer("Something went wrong, sorry. Try again later!");
            }

    state: PaginateFile
        scriptEs6:
            // JAICP does NOT guarantee that the file still exists.
            // The script may have exceeded the storage quota, or the container running the bot may have been restarted.
            // In any of these events, the temporary storage is cleared.
            // It is important that your script always check that the necessary file is still available.
            let pdfContent;
            try {
                pdfContent = await pdfReader.getPdfContent($session.filePath);
            } catch (e) {
                $reactions.answer("Your file is no longer available. Please send it to me again.");
                return;
            }

            const paragraphs = await paragraphBuilder.toArray(pdfContent);
            if (!paragraphs.length || !paragraphs[0]) {
                $reactions.answer("Your file doesn’t contain any text. Try another one.");
                return;
            }

            $reactions.answer(paragraphs[$session.currentPage]);

            const buttons = [];
            if (paragraphs.length > 1 && $session.currentPage > 0) {
                buttons.push({ text: "Prev", transition: "/PaginateFile/Prev" });
            }
            if (paragraphs.length > 1 && $session.currentPage < paragraphs.length - 1) {
                buttons.push({ text: "Next", transition: "/PaginateFile/Next" });
            }
            if (buttons.length > 0) {
                $reactions.inlineButtons(buttons);
            }

        state: Prev
            scriptEs6:
                $session.currentPage--;
                $reactions.transition("/PaginateFile");

        state: Next
            scriptEs6:
                $session.currentPage++;
                $reactions.transition("/PaginateFile");

    state: RejectFile || noContext = true
        event!: fileTooBigEvent
        a: I’m sorry, I can only accept files up to 50 MB in size.

    state: NoMatch || noContext = true
        event!: noMatch
        a: I’m sorry, I didn’t get it. I can only read PDF files but can’t do much else.
