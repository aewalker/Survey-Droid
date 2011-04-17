<STYLE type="text/css">
	.tableTd {
	   	border-width: 0.5pt; 
		border: solid; 
	}
	.tableTdContent{
		border-width: 0.5pt; 
		border: solid;
	}
   
</STYLE>
<table>
	<tr>
		<th colspan=6><big><?php echo $pageName;?></big></th>
	</tr>
	<tr>
		
	</tr>
	<tr>
		<th colspan=4><?php echo $info;?></th>
	</tr>
	<tr>
		<td><b>Date:</b></td>
		<td><?php echo date("F j, Y, g:i a"); ?></td>
	</tr>
	<tr>
		<td><b>Number of Rows:</b></td>
		<td style="text-align:left"><?php echo count($rows);?></td>
	</tr>
	<tr>
		<td></td>
	</tr>
		<tr id="titles">
		<?php	foreach ($columns as $column)
					echo '<td><b>'.$column.'</b></td>';
		?>
		</tr>		
		<?php
		foreach ($rows as $row)
		{
			echo '<tr>';
			foreach ($row as $model => $info)
			{
				foreach ($info as $key => $val)
				{ 
					echo '<td class="tableTdContent">'.$val.'</td>';
				}
			}
			echo '</tr>';
		}

		?>
</table>

