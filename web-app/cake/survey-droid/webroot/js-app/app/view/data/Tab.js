Ext.define("SD.view.data.Tab", {
    alias: 'widget.dataTab',
    extend: "Ext.tab.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template',
        'SD.view.data.AnswersTab', 'SD.view.data.LocationsTab', 'SD.view.data.CallsTab',
        'SD.view.data.StatusChangesTab', 'SD.view.data.SurveysTakenTab'],
    title: 'Data',
    items: [
        {
            itemId: 'answersTab',
            xtype: 'dataAnswersTab'
        },
        {
            itemId: 'locationsTab',
            xtype: 'dataLocationsTab'
        },
        {
            itemId: 'callsTab',
            xtype: 'dataCallsTab'
        },
        {
            itemId: 'statuschangesTab',
            xtype: 'dataStatusChangesTab'
        },
        {
            itemId: 'surveystakenTab',
            xtype: 'dataSurveysTakenTab'
        },
        {
            itemId: 'photosTab',
            title: 'Photos',
            xtype: 'panel',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    itemId: 'photosList',
                    xtype: 'grid',
                    flex: 2,
                    store: 'Extras',
                    columns: [
                        {
                            text: 'Time',
                            dataIndex: 'created',
                            xtype: 'datecolumn',
                            format: 'Y-m-d H:i:s',
                            width: 150
                        }, {
                            text: 'Subject Id',
                            dataIndex: 'subject_id'
                        }, {
                            text: 'Type',
                            xtype: 'templatecolumn',
                            width: 100,
                            dataIndex: 'type',
                            tpl: new Ext.XTemplate(
                                '{[this.getType(values.type)]}', {
                                getType: function(type) {
                                    switch (type) {
                                        case 0: return 'Photos';
                                    }
                                }
                            })
                        }, {
                            text: 'Thumbnail',
                            xtype: 'templatecolumn',
                            flex: 1,
                            tpl: '<img class="thumbnail" src="data:image/jpeg;base64,{data}" />'
                        }
                    ]
                }, {
                    itemId: 'fullImage',
                    xtype: 'panel',
                    flex: 3,
                    title: 'Full size image',
                    html: '<img src="" />',
                    autoScroll: true,
                    bind: function(record) {
                        this.getEl().child('div.x-panel-body').update(
                            '<img class="fullsize" src="data:image/jpeg;base64,'+record.data.data+'" />'
                        );
                        this.doLayout();
                    }
                }
            ]
        }
    ]
});
