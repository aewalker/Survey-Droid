<?php
/*---------------------------------------------------------------------------*
 * models/configuration.php                                                  *
 *                                                                           *
 * Model for phone configuration settings.                                   *
 *---------------------------------------------------------------------------*/
/**
 * Model for the configuration settings used by the phones.
 * 
 * @author Austin Walker
 */
class Configuration extends AppModel
{
	//for php4
	var $name = 'Configuration';
	
	//TODO validate the opt field (must be an operator (eg ==, >=, etc))
	
	function array_inflate($result, $names, $value)
	{
		if (empty($names)) return $value;
		
		if (!array_key_exists($names[0], $result))
			$result[$names[0]] = $this->array_inflate(array(), array_slice($names, 1), $value);
		else
			$result[$names[0]] = $this->array_inflate($result[$names[0]], array_slice($names, 1), $value);
		return $result;
	}
	
	function array_flatten($data)
	{
		$toReturn = array();
		foreach ($data as $key => $val)
		{
			if (is_array($val))
			{
				$sub = array_flatten($val);
				foreach ($sub as $subkey => $subval)
				{
					$toReturn = array_merge($toReturn, array("$key.$subkey" => $subval));
				}
			}
			else
			{
				$toReturn = array_merge($toReturn, array($key => $val));
			}
		}
		return $toReturn;
	}
	
	/**
	 * Returns the result of a find query as an array of key => value pairs.
	 */
	function toKeyVal()
	{
		$data = $this->find('all');
		$toReturn = array();
		foreach ($data as $item)
		{
			$names = explode('.', $item[$name]['c_key']);
			$toReturn = $this->array_inflate($toReturn, $names, $item[$name]['c_value']);
		}
		return $toReturn;
	}
	
	/**
	 * Saves the data in the key => value pair format as given.
	 */
	function fromKeyVal($data)
	{
		if (array_key_exists($name, $data)) $data = $data[$name];
		$data = array_flatten($data);
		foreach ($data as $key => $val)
		{
			if ($this->find('first', array('conditions' => array('c_key' => $key)))) {}
			else { $this->create(); }
			$this->save(array($name => array('c_key' => $key, 'c_value' => $val, 'opt' => '==')));
		}
	}
}
?>