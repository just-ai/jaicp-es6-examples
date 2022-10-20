function allSecretsAndVariablesDefined() {
  try {
    $injector.secrets.forEach((secret) => $secrets.get(secret));
    $injector.variables.forEach((variable) => $env.get(variable));
  } catch (err) {
    return false;
  }
  return true;
}

export default {
  allSecretsAndVariablesDefined,
};
