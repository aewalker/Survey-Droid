/* Global configurations and options */
Ext.Loader.setConfig({enabled: true});
Ext.Date.patterns = {
    ISO8601Long:'Y-m-d H:i:s',
    ISO8601Short:'Y-m-d',
    ShortDate: 'n/j/Y',
    LongDate: 'l, F d, Y',
    FullDateTime: 'l, F d, Y g:i:s A',
    MonthDay: 'F d',
    ShortTime: 'g:i A',
    LongTime: 'g:i:s A',
    SortableDateTime: 'Y-m-d\\TH:i:s',
    UniversalSortableDateTime: 'Y-m-d H:i:sO',
    YearMonth: 'F, Y'
};

//Ext.define('SD.model.Category', {
//    extend: 'Ext.data.Model',
//    fields: [
//        {name: 'id',   type: 'int'},
//        {name: 'name', type: 'string'}
//    ],
//    proxy: {
//        type: 'rest',
//        url: '/'
//    }
//});
//
//Ext.define('SD.model.Product', {
//    extend: 'Ext.data.Model',
//    fields: [
//        {name: 'id',          type: 'int'},
//        {name: 'category_id', type: 'int'},
//        {name: 'name',        type: 'string'}
//    ],
//    // we can use the belongsTo shortcut on the model to create a belongsTo association
//    belongsTo: {type: 'belongsTo', model: 'Category'}
//});
//
//var product = new SD.model.Product({
//    id: 100,
//    category_id: 20,
//    name: 'Sneakers'
//});
//console.log(product);
////product.getCategory(function(category, operation) {
////    //do something with the category object
////    alert(category.get('id')); //alerts 20
////});

Ext.application({
    name: 'SD',
    controllers: ['Surveys', 'Subjects', 'Users', 'Configurations'],
    autoCreateViewport : true,

    launch: function() {
        console.log('App launching');
    }
});