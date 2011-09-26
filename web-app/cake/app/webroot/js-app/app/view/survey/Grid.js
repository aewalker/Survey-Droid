Ext.define("SD.view.survey.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.surveysGrid',
    store: 'Surveys',
    title: 'Surveys List',
    columns: [
        {
            text: 'Survey Name',
            dataIndex: 'name',
            flex: 3
        }, {
            text: 'Date Created',
            dataIndex: 'created',
            xtype: 'datecolumn',
            flex: 2
        }
    ],
    initComponent: function() {
        this.addEvents('editSurveyDetails', 'editSurveyQuestions');
        this.columns.push({
            xtype: 'actioncolumn',
            width: 50,
            sortable: false,
            items: [
                {
                    icon: 'resources/images/cog_edit.png',
                    tooltip: 'Edit Survey Details',
                    handler: function(grid, rowIndex, colIndex, item) {
                        this.fireEvent('editSurveyDetails', grid, rowIndex, colIndex, item);
                    }
                }, {
                    icon: 'resources/images/edit.png',
                    tooltip: 'Edit Survey Questions',
                    handler: function(grid, rowIndex, colIndex, item) {
                        this.fireEvent('editSurveyQuestions', grid, rowIndex, colIndex, item);
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});