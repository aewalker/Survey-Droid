Ext.define('SD.controller.Surveys', {
    extend: 'Ext.app.Controller',
    models: ['Survey'],
    stores: ['Surveys'],
    refs: [
        {ref: 'surveysGrid',    selector: 'surveysTab surveysGrid' },
        {ref: 'surveyDetails',  selector: 'surveysTab surveyDetails' },
        {ref: 'centerRegion',   selector: 'surveysTab panel[region=center]' },
        {ref: 'questionsList',  selector: 'surveysTab #questionsList' }
    ],
    init: function() {
        var me = this;
        me.control({
            'surveysTab surveysGrid': {
                itemclick: function(grid, survey) {
                    console.log("clicking");
                    me.getSurveyDetails().loadRecord(survey);
                    me.getCenterRegion().getLayout().setActiveItem('details');
                },
                itemdblclick: function(grid, survey) {
                    me.getQuestionsList().bindStore(survey.questions());
                    me.getCenterRegion().getLayout().setActiveItem('questionsPanel');
                }
            },
            'surveysTab surveyDetails button[action=save]': {
                click: function() {
                    var form = me.getSurveyDetails().getForm();
                    if (form.isValid()) {
                        form.updateRecord(form.getRecord());
                    }
                }
            }
        })
    },
    onLaunch: function() {
//        var surveys = this.getSurveysStore();
//        surveys.load(function() {
//            var s = surveys.getById(1);
//            console.log(s);
////            s.getFirstQuestion(function(question, operation) {
////                console.log(question);
////            })
//            var questions = s.questions();
//            questions.on('load', function() {
//                console.log(questions);
////                questions.removeAt(3);
////                questions.add({
////                    q_text: 'another question'
////                })
////                questions.sync();
//            })
//
//        })

    }
});