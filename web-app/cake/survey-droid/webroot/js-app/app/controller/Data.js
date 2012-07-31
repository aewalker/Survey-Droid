Ext.define('SD.controller.Data', {
    extend: 'Ext.app.Controller',
    models: ['Answer', 'Location', 'Call', 'StatusChange', 'SurveyTaken', 'Extra'],
    stores: ['Answers', 'Locations', 'Calls', 'StatusChanges', 'SurveysTaken', 'Extras'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'dataTab', selector: '#dataTab' },
        {ref: 'answersSubjectFilter', selector: '#answersTab #subjectFilter' },
        {ref: 'locationsSubjectFilter', selector: '#locationsTab #subjectFilter' },
        {ref: 'callsSubjectFilter', selector: '#callsTab #subjectFilter' },
        {ref: 'statusChangesSubjectFilter', selector: '#statuschangesTab #subjectFilter' },
        {ref: 'surveysTakenSubjectFilter', selector: '#surveystakenTab #subjectFilter' },
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
            '#answersTab button[text=Export]': {
                click: function() {
                    location.href = '/answers/dump';
                }
            },
            '#locationsTab button[text=Export]': {
                click: function() {
                    location.href = '/locations/dump';
                }
            },
            '#callsTab button[text=Export]': {
                click: function() {
                    location.href = '/calls/dump';
                }
            },
            '#statuschangesTab button[text=Export]': {
                click: function() {
                    location.href = '/statuschanges/dump';
                }
            },
            '#surveystakenTab button[text=Export]': {
                click: function() {
                    location.href = '/surveystaken/dump';
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
        this.getDataTab().setActiveTab('answersTab');
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
            subject = this.getAnswersSubjectFilter().getSelectionModel().getSelection()[0];
        answers.clearFilter();
        if (survey)
            filters.push({ property: 'survey_id', value: survey.getId(), exactMatch: true });
        if (subject)
            filters.push({ property: 'subject_id', value: subject.getId(), exactMatch: true });
        if (!Ext.isEmpty(filters))
            answers.filter(filters)
    },
    filterLocations: function() {
        var locations = this.getLocationsStore(),
            subject = this.getLocationsSubjectFilter().getSelectionModel().getSelection()[0];
        locations.clearFilter();
        if (subject)
            locations.filter([{ property: 'subject_id', value: subject.getId(), exactMatch: true }]);
    },
    filterCalls: function() {
        var calls = this.getCallsStore(),
            subject = this.getCallsSubjectFilter().getSelectionModel().getSelection()[0];
        calls.clearFilter();
        if (subject)
            calls.filter([{ property: 'subject_id', value: subject.getId(), exactMatch: true }]);
    },
    filterStatusChanges: function() {
        var statusChanges = this.getStatusChangesStore(),
            subject = this.getStatusChangesSubjectFilter().getSelectionModel().getSelection()[0];
        statusChanges.clearFilter();
        if (subject)
            statusChanges.filter([{ property: 'subject_id', value: subject.getId(), exactMatch: true }]);    },
    filterSurveysTaken: function() {
        var surveysTaken = this.getSurveysTakenStore(),
            subject = this.getSurveysTakenSubjectFilter().getSelectionModel().getSelection()[0];
        surveysTaken.clearFilter();
        if (subject)
            surveysTaken.filter([{ property: 'subject_id', value: subject.getId(), exactMatch: true }]);    }

});