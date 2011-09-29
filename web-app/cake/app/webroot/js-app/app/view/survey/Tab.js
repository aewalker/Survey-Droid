Ext.define("SD.view.survey.Tab", {
    extend: "Ext.panel.Panel",
    requires: ['SD.view.survey.Grid', 'SD.view.survey.Details', 'SD.view.survey.QuestionsPanel'],
    alias: 'widget.surveysTab',
    title: 'Surveys',
    layout: 'border',
    items: [
        {
            xtype: 'surveysGrid',
            region: 'west',
            collapsible: true,
            animCollapse: true,
            split: true,
            flex: 1
        }, {
            region: 'center',
            layout: 'card',
            border: false,
            flex: 3,
            items: [
                {
                    itemId: 'details',
                    xtype: 'surveyDetails'
                }, {
                    itemId: 'questionsPanel',
                    xtype: 'questionsPanel'
                }
            ]
        }
    ]
});