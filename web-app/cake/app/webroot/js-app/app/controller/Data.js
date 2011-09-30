Ext.define('SD.controller.Data', {
    extend: 'Ext.app.Controller',
    models: ['Answer'],
    stores: ['Answers'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'subjectFilter', selector: '#subjectFilter' },
        {ref: 'surveyFilter', selector: '#surveyFilter' }
    ],
    init: function() {
        var me = this;
        me.control({
            '#subjectFilter': {
                selectionchange: me.filterAnswers
            },
            '#surveyFilter': {
                selectionchange: me.filterAnswers
            }
        })
    },
    onLaunch: function() {
        this.getMainTabs().setActiveTab('dataTab');
    },
    filterAnswers: function() {
        var answers = this.getAnswersStore(),
            filters = [],
            survey = this.getSurveyFilter().getSelectionModel().getSelection()[0],
            subject = this.getSubjectFilter().getSelectionModel().getSelection()[0];
        answers.clearFilter();
        if (survey)
            filters.push({ property: 'survey_id', value: survey.getId(), exactMatch: true });
        if (subject)
            filters.push({ property: 'subject_id', value: subject.getId(), exactMatch: true });
        if (!Ext.isEmpty(filters))
            answers.filter(filters)
    }
});