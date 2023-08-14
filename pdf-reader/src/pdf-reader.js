import fs from "fs";
import path from "path";
import axios from "axios";
import { PdfReader } from "pdfreader";

const pdfReader = new PdfReader();

async function downloadPdf(url, userId) {
  // Here we retrieve the path to a persistent file directory.
  // It is always destroyed automatically after request processing has finished.
  // There is also a `$storage.getRequestDir` method that allows access to a per-request temp directory.
  const dir = await $storage.getTempDir();
  const filePath = path.join(dir, `${userId}-${path.basename(url)}`);

  return new Promise((resolve, reject) => {
    axios
      .get(url, { responseType: "stream" })
      .then(({ data }) => {
        const writer = fs.createWriteStream(filePath);
        data.pipe(writer);
        writer.on("finish", () => resolve(filePath));
        writer.on("error", reject);
      })
      .catch(reject);
  });
}

async function getPdfContent(filePath) {
  const items = [];

  return new Promise((resolve, reject) => {
    pdfReader.parseFileItems(filePath, (error, item) => {
      if (error) reject(error);
      else if (!item) resolve(items.map(({ text }) => text).join(" "));
      else if (item.text) items.push(item);
    });
  });
}

export default { downloadPdf, getPdfContent };
