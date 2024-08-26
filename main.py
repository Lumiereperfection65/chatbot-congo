import json
import openai

def load_config():
    """Charge la configuration depuis config.json."""
    with open('config.json', 'r') as config_file:
        return json.load(config_file)

def load_data():
    """Charge les données depuis data.json."""
    with open('data.json', 'r') as data_file:
        return json.load(data_file)

def initialize_openai(config):
    """Initialise l'API OpenAI avec la clé API fournie dans config.json."""
    openai.api_key = config.get("api_key")
    if not openai.api_key:
        raise ValueError("Clé API OpenAI non configurée dans config.json")
    print("API OpenAI initialisée avec succès.")

def generate_response(question, config):
    """Génère une réponse à partir d'une question en utilisant l'API OpenAI."""
    try:
        response = openai.Completion.create(
            model=config.get("model_size", "gpt-3.5-turbo"),
            prompt=f"Question: {question}\nRéponse:",
            max_tokens=config.get("max_tokens", 1500),
            temperature=config.get("temperature", 0.7),
            timeout=config.get("timeout", 10),
        )
        return response.choices[0].text.strip()
    except Exception as e:
        return f"Erreur lors de la génération de la réponse : {str(e)}"

def search_local_data(user_input, data):
    print(type(data))
    """Recherche une réponse locale dans data.json."""
    # Si data est une liste de dicts
    for qa in data:  # data est supposé être une liste
        if isinstance(qa, dict) and user_input.lower() in qa.get("question", "").lower():
            return qa.get("answer")
    return None


def main():
    """Fonction principale pour exécuter le chatbot."""
    # Charger les configurations et les données
    config = load_config()
    data = load_data()

    # Initialiser l'API OpenAI
    initialize_openai(config)

    print("Bienvenue Dans Chatbot-Congo: Tapez votre question ci-dessous. Tapez 'exit' ou 'quit' pour quitter.")
    
    while True:
        user_input = input("Vous: ").strip()
        if user_input.lower() in ['exit', 'quit']:
            print("Chatbot-Congo: Au revoir!")
            break
        
        # Vérifier si une réponse locale existe
        local_answer = search_local_data(user_input, data)
        if local_answer:
            print(f"Chatbot-Congo: {local_answer}")
        else:
            # Si aucune réponse locale n'est trouvée, interroger l'API OpenAI
            openai_answer = generate_response(user_input, config)
            print(f"Chatbot-Congo: {openai_answer}")

if __name__ == "__main__":
    main()
