Ext.define('SD.controller.Subjects', {
    extend: 'Ext.app.Controller',
    models: ['Subject'],
    stores: ['Subjects'],

    refs: [
        {ref: 'subjectsTab',   selector: 'subjectsTab' },
        {ref: 'delSubjectBtn',  selector: 'subjectsTab button[action=delete]' }
    ],

    init: function() {
        var me = this;
        me.control({
            'subjectsTab button[action=add]': {
                click: me.addSubject
            },
            'subjectsTab button[action=delete]': {
                click: me.deleteSubject
            },
            'subjectsTab': {
                selectionchange: me.onSubjectsGridSelectionChange
            }
        });
    },
    onLaunch: function() {
//        console.log(this.getSubjectsGrid());
//        console.log(this.getSubjectsGrid().getPlugin());
    },
    addSubject: function() {
        this.getSubjectsStore().insert(0, new SD.model.Subject());
        this.getSubjectsTab().getPlugin().startEdit(0, 0);
    },
    deleteSubject: function() {
        var selection = this.getSubjectsTab().getSelectionModel().getSelection()[0];
        if (selection)
            this.getSubjectsStore().remove(selection);
    },
    onSubjectsGridSelectionChange: function(selModel, selections) {
        this.getDelSubjectBtn().setDisabled(selections.length === 0);
    }
});