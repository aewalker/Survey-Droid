<?php
/**
 * Routes configuration
 *
 * In this file, you set up routes to your controllers and their actions.
 * Routes are very important mechanism that allows you to freely connect
 * different urls to chosen controllers and their actions (functions).
 *
 * PHP versions 4 and 5
 *
 * CakePHP(tm) : Rapid Development Framework (http://cakephp.org)
 * Copyright 2005-2010, Cake Software Foundation, Inc. (http://cakefoundation.org)
 *
 * Licensed under The MIT License
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright 2005-2010, Cake Software Foundation, Inc. (http://cakefoundation.org)
 * @link          http://cakephp.org CakePHP(tm) Project
 * @package       cake
 * @subpackage    cake.app.config
 * @since         CakePHP(tm) v 0.2.9
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
/**
 * Here, we are connecting '/' (base path) to controller called 'Pages',
 * its action called 'display', and we pass a param to select the view file
 * to use (in this case, /app/views/pages/home.ctp)...
 */
	Router::connect('/', array('controller' => 'pages', 'action' => 'display', 'home'));
/**
 * ...and connect the rest of 'Pages' controller's urls.
 */
	Router::connect('/pages/*', array('controller' => 'pages', 'action' => 'display'));

// Aliasing Api to answers for backwork compatibility

    Router::connect('/answers/:action/*', array('controller' => 'api', 'action' => 'index'));

// RESTful routes
    // Index
    Router::connect("/rest/:controller",
                    array(
                        "[method]" => "GET",
                        "action" => "rest_index"
                    )
    );
    // Create
    Router::connect("/rest/:controller",
                    array(
                        "[method]" => "POST",
                        "action" => "rest_create"
                    )
    );
    // Read
    Router::connect("/rest/:controller/:id",
                    array(
                        "[method]" => "GET",
                        "action" => "rest_read"
                    ),
                    array(
                        "pass" => array("id"),
                        "id" => "[0-9]+"
                    )
    );
    // Update
    Router::connect("/rest/:controller/:id",
                    array(
                        "[method]" => "PUT",
                        "action" => "rest_update"
                    ),
                    array(
                        "pass" => array("id"),
                        "id" => "[0-9]+"
                    )
    );
    // Delete
    Router::connect("/rest/:controller/:id",
                    array(
                        "[method]" => "DELETE",
                        "action" => "rest_delete"
                    ),
                    array(
                        "pass" => array("id"),
                        "id" => "[0-9]+"
                    )
    );