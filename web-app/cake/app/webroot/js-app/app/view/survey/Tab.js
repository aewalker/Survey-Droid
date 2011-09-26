Ext.define("SD.view.survey.Tab", {
    extend: "Ext.panel.Panel",
    requires: ['SD.view.survey.Grid', 'SD.view.survey.DetailsForm'],
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
                    xtype: 'surveysDetailsForm'
                }, {
                    itemId: 'questionsEditor',
//                    xtype: 'grid',
//                    columns: [
//                        {
//                            text: 'Id',
//                            dataIndex: 'id'
//                        }, {
//                            text: 'Question Type',
//                            dataIndex: 'q_type'
//                        }, {
//                            text: 'Question Text',
//                            dataIndex: 'q_text'
//                        }
//                    ]
                    xtype: 'panel',
                    html: 'hellol'
                }
            ]
        }
    ]
});