function allSecretsDefined() {
  try {
    $injector.secrets.forEach((secret) => $secrets.get(secret));
  } catch (err) {
    return false;
  }
  return true;
}

export default { allSecretsDefined };
