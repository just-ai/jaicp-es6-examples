import OpenAI from "openai";

let openai;

function initClient() {
  if (openai) {
    return; // Skip if already initialized
  }
  openai = new OpenAI({
    apiKey: $secrets.get("openaiApiKey"),
  });
}

async function gptChatCompletion(messages) {
  const completion = await openai.chat.completions.create({
    messages,
    model: $env.get("openaiModel", "gpt-3.5-turbo"),
  });
  return completion;
}

export default {
  initClient,
  gptChatCompletion,
};
