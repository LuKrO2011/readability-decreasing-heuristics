import traceback

from common import common
from extractor import Extractor
import os

SHOW_TOP_CONTEXTS = 10
MAX_PATH_LENGTH = 8
MAX_PATH_WIDTH = 2
JAR_PATH = 'JavaExtractor/JPredict/target/JavaExtractor-0.0.1-SNAPSHOT.jar'


def toJavaMethodName(listOfWords):
    # Capitalize the first letter of each word except the first one
    return listOfWords[0] + ''.join(x.title() for x in listOfWords[1:])


def toJavaMethodName2(wordsSeparatedByPipe):
    # Capitalize the first letter of each word except the first one
    return toJavaMethodName(wordsSeparatedByPipe.replace('|', ' ').split(' '))

# Example usage:
# --load models/java14_model/saved_model_iter8.release --predict -i input

class FolderPredictor:

    def __init__(self, config, model):
        model.predict([])
        self.model = model
        self.config = config
        self.path_extractor = Extractor(config,
                                        jar_path=JAR_PATH,
                                        max_path_length=MAX_PATH_LENGTH,
                                        max_path_width=MAX_PATH_WIDTH)

    def read_file(self, input_filename):
        with open(input_filename, 'r') as file:
            return file.readlines()

    def load_folder(self, folder_path):
        for root, dirs, files in os.walk(folder_path):
            for file in files:
                if file.endswith(".java"):
                    yield os.path.join(root, file)

    def predict(self):
        print('Starting folder prediction...')

        predict_path = self.config.PREDICT_PATH if self.config.PREDICT_PATH else 'input'

        for input_filename in self.load_folder(predict_path):
            print("Predicting input_filename: " + input_filename)
            try:
                predict_lines, hash_to_string_dict = self.path_extractor.extract_paths(input_filename)
            except ValueError as e:
                print(e)
                continue

            raw_prediction_results = self.model.predict(predict_lines)
            method_prediction_results = common.parse_prediction_results(
                raw_prediction_results, hash_to_string_dict,
                self.model.vocabs.target_vocab.special_words, topk=SHOW_TOP_CONTEXTS)

            # Print the method prediction results as json
            json_result = "[\n"
            for method_prediction_result in method_prediction_results:
                json_result += "{\n"
                json_result += "\t\"originalName\": \"" + toJavaMethodName2(
                    method_prediction_result.original_name) + "\",\n"
                json_result += "\t\"predictions\": [\n"
                for prediction in method_prediction_result.predictions:
                    json_result += "\t\t{\n"
                    json_result += "\t\t\t\"name\": \"" + toJavaMethodName(prediction["name"]) + "\",\n"
                    json_result += "\t\t\t\"probability\": " + str(prediction["probability"]) + "\n"
                    json_result += "\t\t},\n"

                # Remove the last comma
                json_result = json_result[:-2]

                json_result += "\t]\n"
                json_result += "}"
                json_result += ",\n"

            # Remove the last comma
            json_result = json_result[:-2]
            json_result += "]"

            # Store the json result in the folder of the input file
            # with the same name as the input file except instead of .java it's .json
            output_filename = input_filename[:-5] + ".json"

            with open(output_filename, 'w') as file:
                file.write(json_result)
