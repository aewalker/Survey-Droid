Ext.define("SD.view.survey.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.surveysGrid',
    store: 'Surveys',
    title: 'Surveys List',
    columns: [
        {
            text: 'Id',
            dataIndex: 'id',
            width: 25
        }, {
            text: 'Survey Name',
            dataIndex: 'name',
            flex: 3
        }, {
            text: 'Date Created',
            dataIndex: 'created',
            xtype: 'datecolumn',
            flex: 2
        }
    ]

});