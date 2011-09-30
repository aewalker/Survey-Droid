Ext.define('SD.controller.Configurations', {
    extend: 'Ext.app.Controller',
    models: ['Configuration'],
    stores: ['Configurations'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'settingsTab', selector: '#settingsTab' }
    ],
    init: function() {
        console.log('configurations initting');
    },
    onLaunch: function() {
//        this.getMainTabs().setActiveTab('settingsTab');
//
//        var treeStore = Ext.create('Ext.data.TreeStore', {
//            model: 'SD.model.Subject',
//            root: {
//                text: 'Subjects',
//                leaf: false,
//                expanded: true
//            },
//            listeners: {
//                append: function( thisNode, newChildNode, index, eOpts ) {
//                    if( !newChildNode.isRoot() ) {
//                        newChildNode.set('leaf', true);
//                        newChildNode.set('text', newChildNode.get('first_name'));
////                        newChildNode.set('icon', newChildNode.get('profile_image_url'));
////                        newChildNode.set('cls', 'demo-userNode');
////                        newChildNode.set('iconCls', 'demo-userNodeIcon');
//                    }
//                }
//            }
//        })
//
//        this.getSettingsTab().add({
//            xtype: 'treepanel',
//            store: treeStore
//        });
//        Ext.getStore('Surveys').on('load',  function() {
////            console.log(Ext.getStore('Surveys').getById(1));
//            var survey = Ext.getStore('Surveys').getById(1);
////            treeStore.getRootNode().appendChild(
////                survey
////            );
//
//            survey.set('name', 'tttt survey');
//            survey.save();
////            treeStore.sync();
//        })


    }
});