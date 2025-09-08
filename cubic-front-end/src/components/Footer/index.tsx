import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'SaKiSaKi';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'CSDN',
          title: 'CSDN',
          href: 'https://blog.csdn.net/TIG_JS?spm=1000.2115.3001.5343',
          blankTarget: true,
        },
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined />
              Github
            </>
          ),
          href: 'https://github.com/TIG-prey',
          blankTarget: true,
        },
        // {
        //   key: 'Ant Design',
        //   title: 'Ant Design',
        //   href: 'https://ant.design',
        //   blankTarget: true,
        // },
        {
          key: '粤ICP备2024290876号-1',
          title: '粤ICP备2024290876号-1',
          href: 'https://beian.miit.gov.cn/',
          blankTarget: true,
        },
        {
          key: '粤ICP备2024290876号',
          title: '粤ICP备2024290876号',
          href: 'https://beian.miit.gov.cn/',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
