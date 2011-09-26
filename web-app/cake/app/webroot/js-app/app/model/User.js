Ext.define('SD.model.User', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',         type: 'int', useNull: true},
        {name: 'username',   type: 'string'},
        {name: 'email',      type: 'string'},
        {name: 'password',   type: 'string'},
        {name: 'first_name', type: 'string'},
        {name: 'last_name',  type: 'string'},
        {name: 'admin',      type: 'boolean'}
    ],
    validations: [
        {type: 'presence', field: 'username'},
        {type: 'presence', field: 'admin'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/users',
        reader: {
            type: 'json',
            record: 'User'
        },
        writer: {
            type: 'json',
            root: 'User'
        }
    }
});