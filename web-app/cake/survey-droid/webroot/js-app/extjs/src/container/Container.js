/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @class Ext.container.Container
 * @extends Ext.container.AbstractContainer
 * <p>Base class for any {@link Ext.Component} that may contain other Components. Containers handle the
 * basic behavior of containing items, namely adding, inserting and removing items.</p>
 *
 * <p>The most commonly used Container classes are {@link Ext.panel.Panel}, {@link Ext.window.Window} and {@link Ext.tab.Panel}.
 * If you do not need the capabilities offered by the aforementioned classes you can create a lightweight
 * Container to be encapsulated by an HTML element to your specifications by using the
 * <code><b>{@link Ext.Component#autoEl autoEl}</b></code> config option.</p>
 *
 * {@img Ext.Container/Ext.Container.png Ext.Container component} 
 * <p>The code below illustrates how to explicitly create a Container:<pre><code>
// explicitly create a Container
Ext.create('Ext.container.Container', {
    layout: {
        type: 'hbox'
    },
    width: 400,
    renderTo: Ext.getBody(),
    border: 1,
    style: {borderColor:'#000000', borderStyle:'solid', borderWidth:'1px'},
    defaults: {
        labelWidth: 80,
        // implicitly create Container by specifying xtype
        xtype: 'datefield',
        flex: 1,
        style: {
            padding: '10px'
        }
    },
    items: [{
        xtype: 'datefield',
        name: 'startDate',
        fieldLabel: 'Start date'
    },{
        xtype: 'datefield',
        name: 'endDate',
        fieldLabel: 'End date'
    }]
});
</code></pre></p>
 *
 * <p><u><b>Layout</b></u></p>
 * <p>Container classes delegate the rendering of child Components to a layout
 * manager class which must be configured into the Container using the
 * <code><b>{@link #layout}</b></code> configuration property.</p>
 * <p>When either specifying child <code>{@link #items}</code> of a Container,
 * or dynamically {@link #add adding} Components to a Container, remember to
 * consider how you wish the Container to arrange those child elements, and
 * whether those child elements need to be sized using one of Ext's built-in
 * <b><code>{@link #layout}</code></b> schemes. By default, Containers use the
 * {@link Ext.layout.container.Auto Auto} scheme which only
 * renders child components, appending them one after the other inside the
 * Container, and <b>does not apply any sizing</b> at all.</p>
 * <p>A common mistake is when a developer neglects to specify a
 * <b><code>{@link #layout}</code></b> (e.g. widgets like GridPanels or
 * TreePanels are added to Containers for which no <code><b>{@link #layout}</b></code>
 * has been specified). If a Container is left to use the default
 * {Ext.layout.container.Auto Auto} scheme, none of its
 * child components will be resized, or changed in any way when the Container
 * is resized.</p>
 * <p>Certain layout managers allow dynamic addition of child components.
 * Those that do include {@link Ext.layout.container.Card},
 * {@link Ext.layout.container.Anchor}, {@link Ext.layout.container.VBox}, {@link Ext.layout.container.HBox}, and
 * {@link Ext.layout.container.Table}. For example:<pre><code>
//  Create the GridPanel.
var myNewGrid = new Ext.grid.Panel({
    store: myStore,
    headers: myHeaders,
    title: 'Results', // the title becomes the title of the tab
});

myTabPanel.add(myNewGrid); // {@link Ext.tab.Panel} implicitly uses {@link Ext.layout.container.Card Card}
myTabPanel.{@link Ext.tab.Panel#setActiveTab setActiveTab}(myNewGrid);
 * </code></pre></p>
 * <p>The example above adds a newly created GridPanel to a TabPanel. Note that
 * a TabPanel uses {@link Ext.layout.container.Card} as its layout manager which
 * means all its child items are sized to {@link Ext.layout.container.Fit fit}
 * exactly into its client area.
 * <p><b><u>Overnesting is a common problem</u></b>.
 * An example of overnesting occurs when a GridPanel is added to a TabPanel
 * by wrapping the GridPanel <i>inside</i> a wrapping Panel (that has no
 * <code><b>{@link #layout}</b></code> specified) and then add that wrapping Panel
 * to the TabPanel. The point to realize is that a GridPanel <b>is</b> a
 * Component which can be added directly to a Container. If the wrapping Panel
 * has no <code><b>{@link #layout}</b></code> configuration, then the overnested
 * GridPanel will not be sized as expected.<p>
 *
 * <p><u><b>Adding via remote configuration</b></u></p>
 *
 * <p>A server side script can be used to add Components which are generated dynamically on the server.
 * An example of adding a GridPanel to a TabPanel where the GridPanel is generated by the server
 * based on certain parameters:
 * </p><pre><code>
// execute an Ajax request to invoke server side script:
Ext.Ajax.request({
    url: 'gen-invoice-grid.php',
    // send additional parameters to instruct server script
    params: {
        startDate: Ext.getCmp('start-date').getValue(),
        endDate: Ext.getCmp('end-date').getValue()
    },
    // process the response object to add it to the TabPanel:
    success: function(xhr) {
        var newComponent = eval(xhr.responseText); // see discussion below
        myTabPanel.add(newComponent); // add the component to the TabPanel
        myTabPanel.setActiveTab(newComponent);
    },
    failure: function() {
        Ext.Msg.alert("Grid create failed", "Server communication failure");
    }
});
</code></pre>
 * <p>The server script needs to return a JSON representation of a configuration object, which, when decoded
 * will return a config object with an {@link Ext.Component#xtype xtype}. The server might return the following
 * JSON:</p><pre><code>
{
    "xtype": 'grid',
    "title": 'Invoice Report',
    "store": {
        "model": 'Invoice',
        "proxy": {
            "type": 'ajax',
            "url": 'get-invoice-data.php',
            "reader": {
                "type": 'json'
                "record": 'transaction',
                "idProperty": 'id',
                "totalRecords": 'total'
            })
        },
        "autoLoad": {
            "params": {
                "startDate": '01/01/2008',
                "endDate": '01/31/2008'
            }
        }
    },
    "headers": [
        {"header": "Customer", "width": 250, "dataIndex": 'customer', "sortable": true},
        {"header": "Invoice Number", "width": 120, "dataIndex": 'invNo', "sortable": true},
        {"header": "Invoice Date", "width": 100, "dataIndex": 'date', "renderer": Ext.util.Format.dateRenderer('M d, y'), "sortable": true},
        {"header": "Value", "width": 120, "dataIndex": 'value', "renderer": 'usMoney', "sortable": true}
    ]
}
</code></pre>
 * <p>When the above code fragment is passed through the <code>eval</code> function in the success handler
 * of the Ajax request, the result will be a config object which, when added to a Container, will cause instantiation
 * of a GridPanel. <b>Be sure that the Container is configured with a layout which sizes and positions the child items to your requirements.</b></p>
 * <p>Note: since the code above is <i>generated</i> by a server script, the <code>autoLoad</code> params for
 * the Store, the user's preferred date format, the metadata to allow generation of the Model layout, and the ColumnModel
 * can all be generated into the code since these are all known on the server.</p>
 */
Ext.define('Ext.container.Container', {
    extend: 'Ext.container.AbstractContainer',
    alias: 'widget.container',
    alternateClassName: 'Ext.Container',

    /**
     * Return the immediate child Component in which the passed element is located.
     * @param el The element to test.
     * @return {Component} The child item which contains the passed element.
     */
    getChildByElement: function(el) {
        var item,
            itemEl,
            i = 0,
            it = this.items.items,
            ln = it.length;

        el = Ext.getDom(el);
        for (; i < ln; i++) {
            item = it[i];
            itemEl = item.getEl();
            if ((itemEl.dom === el) || itemEl.contains(el)) {
                return item;
            }
        }
        return null;
    }
});

