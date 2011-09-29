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
                    me.getSurveyDetails().loadRecord(survey);
                    me.getCenterRegion().getLayout().setActiveItem('details');
                },
                itemdblclick: function(grid, survey) {
                    me.getQuestionsList().bindStore(survey.questions());
                    me.getCenterRegion().getLayout().setActiveItem('questionsPanel');
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
        this.getCenterRegion().getLayout().setActiveItem('questionsPanel');
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
        var survey = this.getSurveyDetails().getForm().getRecord();
        if (survey) {
            this.getSurveysStore().remove(survey);
            this.onNewSurveyBtnClick();
        }
    },
    onNewSurveyBtnClick: function() {
        this.getCenterRegion().getLayout().setActiveItem('details');
        this.getSurveyDetails().getForm().reset();
        this.getSurveyDetails().getForm()._record = undefined; // resetting the record, a bit of a hack!
    }
});