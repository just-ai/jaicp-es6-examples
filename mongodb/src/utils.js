function allSecretsAndVariablesDefined() {
  try {
    $injector.secrets.forEach($secrets.get);
    $injector.variables.forEach($env.get);
  } catch (err) {
    return false;
  }
  return true;
}

export default {
  allSecretsAndVariablesDefined,
};
