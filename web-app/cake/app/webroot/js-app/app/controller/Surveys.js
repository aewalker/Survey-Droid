Ext.define('SD.controller.Surveys', {
    extend: 'Ext.app.Controller',
    models: ['Survey'],
    stores: ['Surveys'],
    refs: [
        {ref: 'surveysGrid', selector: 'surveysTab surveysGrid' },
        {ref: 'surveyForm', selector: 'surveysTab form' },
        {ref: 'centerRegion', selector: 'surveysTab panel[region=center]' }
    ],
    init: function() {
        var me = this;
        me.control({
            'surveysTab surveysGrid': {
                selectionchange: me.onSurveysGridSelectionChange
            },
            'surveysTab form button[action=save]': {
                click: me.onSurveysFormSave
            },
            'surveysTab surveysGrid actioncolumn': {
                editSurveyDetails: me.onEditSurveyDetailsClick,
                editSurveyQuestions: me.onEditSurveyQuestionsClick
            }
        })
    },
    onSurveysGridSelectionChange: function(selModel, selections) {
        if (selections[0])
            this.getSurveyForm().loadRecord(selections[0]);
    },
    onSurveysFormSave: function() {
        var form = this.getSurveyForm().getForm();
        if (form.isValid())
            form.updateRecord(form.getRecord());
    },
    onEditSurveyQuestionsClick: function(grid, rowIndex, colIndex, item) {
        var survey = grid.getStore().getAt(rowIndex);
        this.getCenterRegion().getLayout().setActiveItem('questionsEditor');
        console.log(survey);
        return;
        survey.question(function(question, operation) {
            console.log(question);
            console.log(operation);
        });
//        survey.getQuestion(function(question, operation) {
//            console.log(question);
//            console.log(operation);
//        });
    },
    onEditSurveyDetailsClick: function(grid, rowIndex, colIndex, item) {
        var rec = grid.getStore().getAt(rowIndex);
        this.getCenterRegion().getLayout().setActiveItem('details');
        this.getSurveyForm().loadRecord(rec);
    }
});