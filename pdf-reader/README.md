# JAICP ES6 bot example: PDF reader

A bot project which demonstrates the features of the ECMAScript 6 implementation in JAICP.

This bot demonstrates how to use the built-in `$storage` API to manipulate temporary files from the script.
It acts as a reader for PDF files.
The following packages are used in this example:

- [`axios`](https://axios-http.com/) to download files sent by the user and save them to the storage.
- [`pdfreader`](https://www.npmjs.com/package/pdfreader) to parse text content from PDF files.
- [`paragraph-builder`](https://www.npmjs.com/package/paragraph-builder) to paginate the content of long files.

[![Deploy to JAICP](https://just-ai.com/img/deploy-to-jaicp.svg)](https://app.jaicp.com/project-create/jaicp/external)
