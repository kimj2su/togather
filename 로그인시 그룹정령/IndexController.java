package team1.togather.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import team1.togather.domain.Category;
import team1.togather.domain.GroupTab;
import team1.togather.domain.IndexCriteria;
import team1.togather.domain.IndexPage;
import team1.togather.domain.Member;
import team1.togather.domain.WishList;
import team1.togather.service.CategoryService;
import team1.togather.service.GatheringService;
import team1.togather.service.GroupTabService;
import team1.togather.service.MemberService;
import team1.togather.service.WishListService;

@Controller
public class IndexController {
	@Autowired
	private GroupTabService groupTabService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private WishListService wishService;
	@Autowired
	private CategoryService cateService;
	@Autowired
	private GatheringService gatheringService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(HttpSession session, HttpServletRequest request, IndexCriteria cri) {
		List<GroupTab> list = new ArrayList<>();
		List<Long> groupMemberCount = new ArrayList<>();
		List<String> namelist = new ArrayList<>();
		Map<String,Object> map = new HashMap<String,Object>();
		Member m = (Member)session.getAttribute("m");
		Long viewCheck =null;
		ModelAndView mv = new ModelAndView();
		if(m!=null) {
			if(request.getParameter("page")!=null) {
				String pageAt = request.getParameter("page");
				cri.setPage(Integer.parseInt(pageAt));
			}
			if(request.getParameter("pageSize")!=null) {
				String pageSize = request.getParameter("pageSize");
				cri.setPageSize(Integer.parseInt(pageSize));
			}
			IndexPage pm = new IndexPage();
			pm.setCri(cri);
			pm.setTotalCount(groupTabService.pageCount(m));
			mv.addObject("pm", pm);
			mv.addObject("cri", cri);
			long mnum = m.getMnum();
			map.put("startRow",cri.getStartRow());
			map.put("endRow",cri.getEndRow());
			map.put("mnum",m.getMnum());
			list.addAll(groupTabService.loginGroupList(map));
			if(list.size()==0) {
				list = groupTabService.selectAllS(cri);
				pm.setTotalCount(groupTabService.notCategorypageCount());
				namelist = groupTabService.NoCategoryNames(cri);
			}
			namelist=groupTabService.groupMemberNames(map);
			viewCheck = memberService.messageViewCheck(mnum);//로그인 되었으면 가져오는데 알림을 안꺼놓은거의 갯수 		
		}else {
			viewCheck=null;
			list = groupTabService.selectAllS(cri);
			namelist = groupTabService.NoCategoryNames(cri);
			
		}
		long membercount = memberService.memberCount();
		
		long gatheringcount = gatheringService.gatheringCount();
		for(int i =0;i<list.size();i++) {
			groupMemberCount.add(groupTabService.groupMemberCount(list.get(i).getGseq()));
		}
		long groupcount = groupTabService.groupCount();
		mv.addObject("list", list);
		mv.addObject("namelist", namelist);
		mv.addObject("membercount",membercount);
		mv.addObject("groupcount",groupcount);
		mv.addObject("groupMemberCount", groupMemberCount);
		mv.addObject("message", viewCheck);
		mv.addObject("gatheringcount", gatheringcount);
		mv.setViewName("index");
		return mv;
	}
	@ResponseBody
	@PostMapping("viewChecked")
	public String viewChecked(long mnum) {
		memberService.viewChecked(mnum);
		return "ok";
	}
	@ResponseBody
	@PostMapping("handleWishList")
	public WishList handleWishList(@RequestBody HashMap<String, Object> map, HttpSession session) {
		Member member = (Member)session.getAttribute("m");
		
		return null;
	}
	@ResponseBody
	@GetMapping("showInCategory")
	public List<Category> showInCategory(String int_out){
		int_out = int_out.trim();
		List<Category> cateList = cateService.getCategoryByOutCatogory(int_out);
		return cateList;
	}
	@ResponseBody
	@GetMapping("showGroups")
	public List<GroupTab> showGroups(String category){
		System.out.println("인덱스컨트롤러 쇼그룹들어옴");
		System.out.println("category: "+ category);
		category = category.trim();
		List<GroupTab> cateList = cateService.getGroupsByCategory(category);
		return cateList;
	}
	
	
	
}
