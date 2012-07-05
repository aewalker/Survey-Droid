# locations
create index locations_created_index on locations(created);
create index locations_subject_id_index on locations(subject_id);

# answers_choices
create index answers_choices_answer_id_index on answers_choices(answer_id);
create index answers_choices_choice_id_index on answers_choices(choice_id);

# answers
create index answers_question_id_index on answers(question_id);
create index answers_subject_id_index on answers(subject_id);
create index answers_created_index on answers(created);

# surveys_taken
create index surveys_taken_subject_id_index on surveys_taken(subject_id);
create index surveys_taken_survey_id_index on surveys_taken(survey_id);
create index surveys_taken_created_index on surveys_taken(created);

# choices
create index choices_question_id_index on choices(question_id);

# extras
create index extras_subject_id_index on extras(subject_id);

# questions
create index questions_survey_id_index on questions(survey_id);

# surveys
create index surveys_created_index on surveys(created);

# status_changes
create index status_changes_subject_id_index on status_changes(subject_id);
create index status_changes_created_index on status_changes(created);

# conditions
create index conditions_branch_id_index on conditions(branch_id);
create index conditions_question_id_index on conditions(question_id);
create index conditions_choice_id_index on conditions(choice_id);

# calls
create index calls_subject_id_index on calls(subject_id);
create index calls_created_index on calls(created);

# branches
create index branches_question_id_index on branches(question_id);

