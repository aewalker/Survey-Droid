<?php
/*****************************************************************************
 * views/answers/pull.ctp                                                    *
 *                                                                           *
 * Lets phones pull survey data from the webserver.                          *
 *****************************************************************************/

//test data for now
echo '{
    "surveys": [
        {
            "id": 1,
            "name": "The Meaning of Life",
            "created": 1302885536,
            "question_id": 1,
            "mo": "1200,1400",
            "tu": "", 
            "we": "",
            "th": "",
            "fr": "2000",
            "sa": "",
            "su": ""
        }
    ],
    "questions": [
        {
            "id": 1,
            "survey_id": 1,
            "q_text": "Shall we follow the gord?"
        }, {
            "id": 2,
            "survey_id": 1,
            "q_text": "What side of life do you always look on?"
        }
    ],
    "branches": [
        {
            "id": 1,
            "question_id": 1,
            "next_q": 2
        }, {
            "id": 2,
            "question_id": 1,
            "next_q": 1
        }
    ],
    "conditions": [
        {
            "id": 1,
            "branch_id": 1,
            "question_id": 1,
            "choice_id" : 1,
            "type": 0
        }, {
            "id": 2,
            "branch_id": 2,
            "question_id": 1,
            "choice_id" : 2,
            "type": 0
        }
    ],
    "choices": [
        {
            "id": 1,
            "choice_test": "Yes!",
            "question_id": 1
        }, {
            "id": 2,
            "choice_test": "No!",
            "question_id": 1
        }
    ]
}';
?>