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
            itemId: 'surveyEditor',
            region: 'center',
            xtype: 'panel',
            title: 'Survey Title',
            flex: 3,
            layout: 'fit',
            items: [{

                xtype: 'tabpanel',
                border: false,

                items: [
                    {
                        itemId: 'details',
                        xtype: 'surveyDetails',
                        title: 'Survey Details'
                    }, {
                        itemId: 'questionsPanel',
                        xtype: 'questionsPanel',
                        title: 'Survey Questions'
                    }
                ]
            }]
        }

    ]
});