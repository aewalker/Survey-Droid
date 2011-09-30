Ext.define('SD.controller.Data', {
    extend: 'Ext.app.Controller',
    models: ['Answer', 'Location', 'Call'],
    stores: ['Answers', 'Locations', 'Calls'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'dataTab', selector: '#dataTab' },
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
            },
            '#callTab': {
                activate: function() { me.loadIfEmpty('Calls'); }
            },
            '#locationTab': {
                activate: function() { me.loadIfEmpty('Locations'); }
            },
            '#answerTab': {
                activate: function() { me.loadIfEmpty('Answers'); }
            }
        })
    },
    onLaunch: function() {
//        this.getMainTabs().setActiveTab('dataTab');
//        this.getDataTab().setActiveTab('callTab');
    },
    loadIfEmpty: function(storeName) {
        var store = Ext.getStore(storeName);
        if (!store.isLoading() && store.count() == 0)
        store.load();
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