<?php
/*---------------------------------------------------------------------------*
 * app_model.php                                                             *
 *                                                                           *
 * Holds application-wide model-related code.                                *
 *---------------------------------------------------------------------------*/
/**
 * Holds model functionality that can be used across all models.
 * 
 * @author Austin Walker
 */
class AppModel extends Model
{
	//checks that two fields are equal
	//code by aranworld:
	//http://bakery.cakephp.org/articles/aranworld/2008/01/14/
	//using-equalto-validation-to-compare-two-form-fields
	function identicalFieldValues( $field=array(), $compare_field=null ) 
    {
        foreach( $field as $key => $value ){
            $v1 = $value;
            $v2 = $this->data[$this->name][ $compare_field ];                 
            if($v1 !== $v2) {
                return FALSE;
            } else {
                continue;
            }
        }
        return TRUE;
    }
}
?>