package top.forforever.yygh.vo.cmn;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * <p>
 * Dict
 * </p>
 *
 * @author qy
 */
@Data
public class DictEeVo {

	@ExcelProperty(value = "id" ,index = 0)
	@ColumnWidth(10)
	private Long id;

	@ExcelProperty(value = "上级id" ,index = 1)
	@ColumnWidth(10)
	private Long parentId;

	@ExcelProperty(value = "名称" ,index = 2)
	@ColumnWidth(15)
	private String name;

	@ExcelProperty(value = "值" ,index = 3)
	@ColumnWidth(15)
	private String value;

	@ExcelProperty(value = "编码" ,index = 4)
	@ColumnWidth(15)
	private String dictCode;

}

