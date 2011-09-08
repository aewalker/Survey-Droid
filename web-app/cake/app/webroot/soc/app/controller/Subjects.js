Ext.define('Soc.controller.Subjects', {
    extend: 'Ext.app.Controller',
    models: ['Subject'],
    stores: ['Subjects'],

    refs: [
        {ref: 'subjectsGrid',   selector: 'subjectsGrid' },
        {ref: 'delSubjectBtn',  selector: 'subjectsGrid button[action=delete]' }
    ],

    init: function() {
        var me = this;
        me.control({
            'subjectsGrid button[action=add]': {
                click: me.addSubject
            },
            'subjectsGrid button[action=delete]': {
                click: me.deleteSubject
            },
            'subjectsGrid': {
                selectionchange: me.onSubjectsGridSelectionChange
            }
        });
    },
    onLaunch: function() {
//        console.log(this.getSubjectsGrid());
//        console.log(this.getSubjectsGrid().getPlugin());
    },
    addSubject: function() {
        this.getSubjectsStore().insert(0, new Soc.model.Subject());
        this.getSubjectsGrid().getPlugin().startEdit(0, 0);
    },
    deleteSubject: function() {
        var selection = this.getSubjectsGrid().getSelectionModel().getSelection()[0];
        if (selection)
            this.getSubjectsStore().remove(selection);
    },
    onSubjectsGridSelectionChange: function(selModel, selections) {
        this.getDelSubjectBtn().setDisabled(selections.length === 0);
    }
});