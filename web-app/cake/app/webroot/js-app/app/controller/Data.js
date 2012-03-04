Ext.define('SD.controller.Data', {
    extend: 'Ext.app.Controller',
    models: ['Answer', 'Location', 'Call', 'StatusChange', 'SurveyTaken', 'Extra'],
    stores: ['Answers', 'Locations', 'Calls', 'StatusChanges', 'SurveysTaken', 'Extras'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'dataTab', selector: '#dataTab' },
        {ref: 'subjectFilter', selector: '#subjectFilter' },
        {ref: 'surveyFilter', selector: '#surveyFilter' },
        {ref: 'imageViewer', selector: '#photosTab #fullImage' }
    ],
    init: function() {
        var me = this;
        me.control({
            '#answersTab #subjectFilter': {
                selectionchange: me.filterAnswers
            },
            '#answersTab #surveyFilter': {
                selectionchange: me.filterAnswers
            },
            '#locationsTab #subjectFilter': {
                selectionchange: me.filterLocations
            },
            '#callsTab #subjectFilter': {
                selectionchange: me.filterCalls
            },
            '#statuschangesTab #subjectFilter': {
                selectionchange: me.filterStatusChanges
            },
            '#surveystakenTab #subjectFilter': {
                selectionchange: me.filterSurveysTaken
            },
            '#callsTab': {
                activate: function() { me.loadIfEmpty('Calls'); }
            },
            '#locationsTab': {
                activate: function() { me.loadIfEmpty('Locations'); }
            },
            '#locationsTab button[text=Export]': {
                click: function() {
                    location.href = '/locations/dump';
                }
            },
            '#answersTab': {
                activate: function() { me.loadIfEmpty('Answers'); }
            },
            '#statuschangesTab': {
                activate: function() { me.loadIfEmpty('StatusChanges'); }
            },
            '#surveystakenTab': {
                activate: function() { me.loadIfEmpty('SurveysTaken'); }
            },
            '#photosTab': {
                activate: function() { me.loadIfEmpty('Extras'); }
            },
            '#photosTab #photosList': {
                itemclick: function(grid, record) {
                    me.getImageViewer().bind(record);
                }
            }
        })
    },
    onLaunch: function() {
        this.getMainTabs().setActiveTab('dataTab');
        this.getDataTab().setActiveTab('locationsTab');
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
    },
    filterLocations: function() {
        console.log("filtering locations");
    },
    filterCalls: function() {
        console.log("filtering calls");
    },
    filterStatusChanges: function() {
        console.log("filtering status changes");
    },
    filterSurveysTaken: function() {
        console.log("filtering surveys taken");
    }

});