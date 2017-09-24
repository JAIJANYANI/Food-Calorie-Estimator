from __future__ import print_function, division
from flask import Flask , redirect , url_for , request , render_template , jsonify , json
import pandas as pd 
import os
import random
from collections import Counter
import numpy as np
import tensorflow as tf
import json
import sys 



app = Flask(__name__)


# @app.route("/predict" , methods=['POST'])
@app.route('/'  , methods=['POST'])

def hello():
    data = request.data
    # query = str(json_)
    print(data)
    imagePath = data
    modelFullPath = './output_graph.pb'
    labelsFullPath = './output_labels.txt'





    def create_graph():
        """Creates a graph from saved GraphDef file and returns a saver."""
        # Creates graph from saved graph_def.pb.
        with tf.gfile.FastGFile(modelFullPath, 'rb') as f:
            graph_def = tf.GraphDef()
            graph_def.ParseFromString(f.read())
            _ = tf.import_graph_def(graph_def, name='')
    def run_inference_on_image():
        answer = None

        if not tf.gfile.Exists(imagePath):
            tf.logging.fatal('File does not exist %s', imagePath)
            return answer

        image_data = tf.gfile.FastGFile(imagePath, 'rb').read()

        # Creates graph from saved GraphDef.
        create_graph()

        with tf.Session() as sess:

            softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')
            predictions = sess.run(softmax_tensor,
                                {'DecodeJpeg/contents:0': image_data})
            predictions = np.squeeze(predictions)

            top_k = predictions.argsort()[-1:][::-1]  # Getting top 5 predictions
            f = open(labelsFullPath, 'rb')
            lines = f.readlines()
            labels = [str(w).replace("\n", "") for w in lines]
            for node_id in top_k:
                human_string = labels[node_id]
                score = predictions[node_id]
                # return '%s (score = %.5f)' % (human_string, score)

            answer = labels[top_k[0]]
            return answer
    answer = run_inference_on_image()
    return '%s' % answer



    # if __name__ == '__main__':
    
    


if __name__ == '__main__':
    # imagePath = './image1.jpg'
    modelFullPath = './output_graph.pb'
    labelsFullPath = './output_labels.txt'
   # app.run(debug = True)
    app.run('0.0.0.0' , 5000)
