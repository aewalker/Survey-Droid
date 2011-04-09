<?php
/*****************************************************************************
 * views/helpers/table.php                                                   *
 *                                                                           *
 * The TableHelper allows you to easily format complex layered data.         *
 *****************************************************************************/
class TableHelper extends Helper
{
	var $model = NULL;
	
	var $helpers = array('Html');
	
	//returns a string that is the header of a display table
	//$style is as below.
	function startTable($model = NULL, $style = array())
	{
		if (empty($model)) throw new Exception('Must provide a model name');
		$this->model = $model;
		$s = '<table'.$this->_getHTMLVal($style, 'table').'>';
		
		$s = $s.'<tr'.$this->_getHTMLVal($style, 'tr').'>';
		
		$s = $s.'<th'.$this->_getHTMLVal($style, 'th').'>';
		$s = $s.Inflector::pluralize($model).'</th></tr>';
		return $s;
	}
	
	//returns a string that is the body of the display table
	//$results holds the data (takes data in the form that find() returns),
	//$commands holds what options to give for each row in the form of
	//'printed command' => array('command' => 'controller function', 'arg' => 'model field', 'type' => 'link or ajax')
	//$fields is the list of fields to print out; if the empty array (the default), prints all
	//$style takes the form of array('tag' => array('element' => 'value')) and adds 'value' as
	//the value of the HTML attribute 'tag' for the 'element' tags
	function tableBody($results,
		$commands = array
		(
			'Edit' => array('command' => 'edit', 'arg' => 'id', 'type' => 'link'),
			'Delete' => array('command' => 'delete', 'arg' => 'id', 'type' => 'link')
		),
		$fields = array(), $style = array())
	{
		if (empty($this->model)) throw new Exception('Must call startTable() before tableBody()');
		$s = '';
		foreach ($results as $result) foreach ($result as $model => $info)
		{
			if ($model == $this->model)
			{
				$s = $s.'<tr'.$this->_getHTMLVal($style, 'tr').'>';
				foreach ($info as $key => $val)
				{
					if (in_array($key, $fields) || empty($fields))
					{
						$s = $s.'<td'.$this->_getHTMLVal($style, 'td').'>';
						$s = $s.htmlspecialchars($val).'</td>';
					}
				}
				foreach ($commands as $command => $val)
				{
					$s = $s.'<td'.$this->_getHTMLVal($style, 'td').'>';
					if ($val['type'] == 'link')
					{
						$s = $s.$this->Html->link($command, array
						(
							'controller' => $this->_getURLName($this->model),
							'action' => $val['command'],
							$info[$val['arg']])
						);
					}
					else if ($val['type'] == 'ajax')
					{
						$s = $s.$this->Js->link($command, $this->_getURLName($this->model).DS
							.$val['command'].DS
							.$info[$val['arg']]);
					}
					$s = $s.'</td>';
				}
				$s = $s.'</tr>';
			}
		}
		return $s;
	}
	
	//returns a string that is a the end of a display table.  Also adds commands to be added
	//as links after the body of the table.  $commands and $style are as above
	function endTable($commands = array('Add' => array('command' => 'add', 'type' => 'link')), $style = array())
	{
		if (empty($this->model)) throw new Exception('Must call startTable() before endTable()');
		$s = '</table>';
		foreach ($commands as $command => $val)
		{
			$s = $s.'<div'.$this->_getHTMLVal($style, 'td').'>';
			if ($val['type'] == 'link')
			{
				$s = $s.$this->Html->link($command,
					array('controller' => $this->_getURLName($this->model), 'action' => $val['command']));
			}
			else if ($val['type'] == 'ajax')
			{
				$s = $s.$this->Js->link($command, $this->_getURLName($this->model).DS
					.$val['command'];
			}
			$s = $s.'</div>';
		}
		return $s;
	}
	
	//returns an HTML attrubte string as specified in $var (formated as $style above) for $key
	function _getHTMLVal($var, $key)
	{
		$s = '';
		foreach ($var as $type)
		{
			if (isset($type[$key]))
				$s = $s." $type=\"".htmlspecialchars($type[$key]);
		}
		return $s;
	}
	
	//wrapper for Inflector functions to change a singular, CamelCase word into a plural, underscored word
	function _getURLName($word)
	{
		return Inflector::underscore(Inflector::pluralize($word));
	}
}
?>