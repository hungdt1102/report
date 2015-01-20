<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- start: Main Menu -->
			<div id="sidebar-left" class="span2">
				<div class="nav-collapse sidebar-nav">
					<ul class="nav nav-tabs nav-stacked main-menu">
						<li><a href="home.htm"><i class="icon-home"></i><span class="hidden-tablet"> Trang chủ</span></a></li>
						<li>
							<a class="dropmenu" href="#"><i class="icon-bar-chart"></i><span class="hidden-tablet"> Thống kê SMS</span></a>
							<ul>
								<li><a class="submenu" href="#"><i class="icon-plus"></i><span class="hidden-tablet"> Thống kê theo DV</span></a></li>
								<li><a class="submenu" href="#"><i class="icon-plus"></i><span class="hidden-tablet"> Thống kê theo thời gian</span></a></li>
								<c:if test="${rolename == 'admin' }">
								<li><a class="submenu" href="#"><i class="icon-plus"></i><span class="hidden-tablet"> Thống kê theo đối tác</span></a></li>
								</c:if>
							</ul>	
						</li>
						<c:if test="${rolename == 'admin' }">
						<li>
							<a class="dropmenu" href="#"><i class="icon-cog"></i><span class="hidden-tablet"> Cấu hình</span></a>
							<ul>
								<li><a class="submenu" href="#"><i class="icon-plus"></i><span class="hidden-tablet"> Quản lý đối tác</span></a></li>
								<li><a class="submenu" href="#"><i class="icon-plus"></i><span class="hidden-tablet"> Cấu hình website</span></a></li>
							</ul>	
						</li>
						</c:if>
					</ul>
				</div>
			</div>
			<!-- end: Main Menu -->