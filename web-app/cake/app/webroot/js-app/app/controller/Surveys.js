Ext.define('SD.controller.Surveys', {
    extend: 'Ext.app.Controller',
    models: ['Survey'],
    stores: ['Surveys'],
    refs: [
        {ref: 'surveysGrid',    selector: 'surveysTab surveysGrid' },
        {ref: 'surveyDetails',  selector: 'surveysTab surveyDetails' },
        {ref: 'tabPanel',   selector: 'surveysTab tabpanel' },
        {ref: 'surveyEditor', selector: 'surveysTab #surveyEditor' },
        {ref: 'questionsList',  selector: 'surveysTab #questionsList' },
        {ref: 'questionsPanel', selector: 'surveysTab questionsPanel' }
    ],
    init: function() {
        var me = this;
        me.control({
            'surveysTab surveysGrid': {
                itemclick: function(grid, survey) {
                    me.getSurveyDetails().loadRecord(survey);
                    me.getSurveyEditor().setTitle('Survey Editor - ' + survey.data.name);
                    me.getTabPanel().setActiveTab('details');
//                    me.getSurveysGrid().collapse();
                }
            },
            'surveysTab questionsPanel': {
                activate: function() {
                    var survey = this.getSurveyDetails().getRecord();
                    if (survey)
                        me.getQuestionsList().bindStore(survey.questions());
                }
            },
            'surveysTab surveyDetails button[action=save]': {
                click: me.onSurveySaveBtnClick
            },
            'surveysTab surveyDetails button[action=delete]': {
                click: me.onSurveyDeleteBtnClick
            },
            'surveysGrid button[action=new]': {
                click: me.onNewSurveyBtnClick
            }
        })
    },
    onLaunch: function() {
//        this.getTabPanel().setActiveTab('questionsPanel');
    },
    onSurveySaveBtnClick: function() {
        var form = this.getSurveyDetails().getForm();
        if (form.isValid()) {
            if (form.getRecord())
                form.updateRecord(form.getRecord());
            else
                this.getSurveysStore().add(form.getValues());
        }
    },
    onSurveyDeleteBtnClick: function() {
        var form = this.getSurveyDetails().getForm(),
            survey = form.getRecord();
        if (survey) {
            this.getSurveysStore().remove(survey);
            form.reset();
            form._record = undefined;
        }
    },
    onNewSurveyBtnClick: function() {
        this.getTabPanel().setActiveTab('details');
        this.getSurveyDetails().getForm().reset();
        this.getSurveyDetails().getForm()._record = undefined; // resetting the record, a bit of a hack!
    }
});