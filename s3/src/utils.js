function allSecretsAndVariablesDefined() {
  try {
    $injector.secrets.forEach($secrets.get);
    $injector.variables.forEach($env.get);
  } catch (err) {
    return false;
  }
  return true;
}

function streamToString(stream) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    stream.on("data", (chunk) => chunks.push(chunk));
    stream.on("error", reject);
    stream.on("end", () => resolve(Buffer.concat(chunks).toString("utf8")));
  });
}

export default {
  allSecretsAndVariablesDefined,
  streamToString,
};
